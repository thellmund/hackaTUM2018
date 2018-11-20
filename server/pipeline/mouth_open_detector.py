class MouthOpenDetector(object):

    @staticmethod
    def detect(landmarks, face_rect):
        top_indices =  [49, 50, 51, 52, 53, 54, 55, 65, 64, 63, 62, 61]
        bottom_indices = [55, 56, 57, 58, 59, 60, 49, 61, 68, 67, 66, 65]

        face_width = face_rect[1] - face_rect[3]

        top_lip = landmarks[0]["top_lip"]
        bottom_lip = landmarks[0]["bottom_lip"]

        top_lip_y = list(map(lambda k: k[1], top_lip))
        bottom_lip_y = list(map(lambda k: k[1], bottom_lip))

        top_lip_left = top_lip[10][1]
        top_lip_middle = top_lip[9][1]
        top_lip_right = top_lip[8][1]
        bottom_lip_left = bottom_lip[8][1]
        bottom_lip_middle = bottom_lip[9][1]
        bottom_lip_right = bottom_lip[10][1]

        deltas = [
            bottom_lip_left - top_lip_left, 
            bottom_lip_middle - top_lip_middle, 
            bottom_lip_right - top_lip_right
        ]

        delta_sum = sum(deltas)
        return (delta_sum / face_width) > 0.1
