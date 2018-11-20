import face_recognition
import dlib

detector = dlib.get_frontal_face_detector()

class FaceCropper(object):

    @staticmethod
    def crop_faces(img):
        results = []
        face_rects = detector(img, 0)

        print("[FACE LOCATIONS] ", face_rects)
        tuples = []
        for rect in face_rects:
            _tuple = FaceCropper.rect_to_tuple(rect)
            tuples.append(_tuple)
            
            t, r, b, l = _tuple
            face = img[t:b, l:r]
            results.append(face)

        return results, tuples

    @staticmethod
    def rect_to_tuple(rect): 
        """Converts rect to tuple of (top, right, bottom, left)"""
        return (rect.top(), rect.right(), rect.bottom(), rect.left())
