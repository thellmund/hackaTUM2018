import face_recognition
from .store import Store

class Reidentifier(object): 

    @staticmethod
    def reidentify(face_encoding):
        """returns the id of the identified person or -1 if not match was found"""
        for id in Store.store["face_encodings"].keys():
            unknown_face_encoding = Store.store["face_encodings"][id]
            results = face_recognition.compare_faces([face_encoding], unknown_face_encoding)
            if results[0]:
                print("[MATCHED] ", str(id))
                return id
        return None
