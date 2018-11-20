import base64
import face_recognition
import cv2
import numpy as np

class FaceEncoder(object): 

    @staticmethod
    def decode(base64_string):
        decoded_value = base64.b64decode(base64_string)
        d = np.fromstring(decoded_value, np.uint8)
        result = cv2.imdecode(d, cv2.IMREAD_COLOR)
        return result
        
    @staticmethod
    def get_face_encoding_or_none(face_img):
        encoding = face_recognition.face_encodings(face_img)
        if len(encoding) == 0:
            return []
        return encoding[0]
