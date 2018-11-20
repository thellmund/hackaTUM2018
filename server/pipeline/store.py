import face_recognition
import cv2
import dlib

class Store(object): 

    store = {
        "face_encodings": {},
        "talking_histories": {}
    }
    
    @staticmethod
    def get_encoding_or_none(id): 
        try:
            result = Store.store["face_encodings"][id]
            return result
        except KeyError:
            return None

    @staticmethod
    def get_talking_or_none(id):
        try:
            result = Store.store["talking_histories"][id]
            return result
        except KeyError:
            return None
    
    @staticmethod
    def store_encoding(id, encoding):
        Store.store["face_encodings"][id] = encoding

    def store_talking(id, talking):
        try:
            Store.store["talking_histories"][id].append(talking)
        except KeyError:
            Store.store["talking_histories"][id] = [talking]
