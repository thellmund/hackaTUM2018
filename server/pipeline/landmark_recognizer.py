import face_recognition

class LandmarkRecognizer(object): 

    @staticmethod
    def recognize(person_face_image): 
        landmarks = face_recognition.face_landmarks(person_face_image)
        return landmarks
