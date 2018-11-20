from aiohttp import web
from data.request import Request
import aiohttp
import asyncio
import socket
import json
from zeroconf import Zeroconf, ServiceInfo, ZeroconfServiceTypes
import os
script_dir = os.path.dirname(__file__)
import sys
sys.path.append("/pipeline")

from pipeline.face_encoder import FaceEncoder
from pipeline.face_cropper import FaceCropper
from pipeline.landmark_recognizer import LandmarkRecognizer
from pipeline.reidentifier import Reidentifier
from pipeline.talk_identifier import TalkingIdentifier
from pipeline.store import Store
from pipeline.talk_identifier import TalkingIdentifier
import time

async def websocket_handler(request):
    ws = web.WebSocketResponse()
    print('preparing socket')
    await ws.prepare(request)
    response_number = -1
    print('connection established')

    async for msg in ws:
        print("********* new message")
        if msg.type == aiohttp.WSMsgType.TEXT:
            if msg.data == 'close':
                await ws.close()
            else:
                response_number += 1
                response = handle_request(msg.data, response_number)
                print(response)
                await ws.send_str(response)
        elif msg.type == aiohttp.WSMsgType.ERROR:
            print('ws connection closed with exception %s' % ws.exception())

    print('websocket connection closed')

    return ws

def handle_request(text, response_number):
    request = json.loads(text)
    
    try:
        image_data = request["image_data"]
        print("len of request ", len(image_data))
    except:
        response = MockClient.create_mock_response(timestamp, response_number)
        return response
    
    is_talking = pipe(image_data)
    timestamp = ""
    
    try:
        timestamp = request["timestamp"]
    except:
        timestamp = "0"
    
    json_obj = {
        'timestamp': timestamp,
        'talking': is_talking
    }

    json_data = json.dumps(json_obj)
    return json_data

def pipe(image_data):
    talking = []
    image = FaceEncoder.decode(image_data)
    
    # get all cropped faces
    cropped_faces, face_locations = FaceCropper.crop_faces(image)
    
    for face_img, face_location in zip(cropped_faces, face_locations):
        print("[FACE DETECTED]")
        
        # get face_encodings
        encoding = FaceEncoder.get_face_encoding_or_none(face_img)
        if len(encoding) == 0:
            continue
        
        # reidentify
        id = Reidentifier.reidentify(encoding)
        # not found?
        if id is None:
            try:
                print("[NO ID FOUND]")
                print(Store.store["face_encodings"].keys())
                id = max(map(int, Store.store["face_encodings"].keys())) + 1
                print("[CREATED ID] ", str(id))
            except:
                print("[CREATED FIRST ID]")
                # first id
                id = 0
            Store.store_encoding(id, encoding)
        
        # get landmarks
        landmarks = LandmarkRecognizer.recognize(face_img)
        # check if talking
        is_talking = TalkingIdentifier.identify(id, landmarks, face_location)
        if is_talking:
            talking.append(id)
    
    return talking

def main():
    server_ip_address = '0.0.0.0'
    port = 8000
    
    try:
        local_ip_address = socket.gethostbyname(socket.gethostname())
    except Exception as e:
        print('Error u  sing host name in server setup')
        local_ip_address = socket.gethostbyname('')
        
    loop = asyncio.get_event_loop()

    app = web.Application(loop = loop)
    app.add_routes([
        web.get('/ws', websocket_handler)
    ])

    server_generator = loop.create_server(app.make_handler(), server_ip_address, port)
    server = loop.run_until_complete(server_generator)

    try:
        print('Server is running at {0}:{1}'.format(local_ip_address, 8000))
        loop.run_forever()
    except Exception as e:
        print(e)
        pass
    finally:
        loop.close()
        print('Server closed.')
    
if __name__ == '__main__':
    main()



