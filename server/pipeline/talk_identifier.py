from .store import Store
from .mouth_open_detector import MouthOpenDetector

class TalkingIdentifier(object): 

    threshold = 0.2

    @staticmethod
    def identify(id, landmarks, face_location):
        Store.store_talking(id, MouthOpenDetector.detect(landmarks, face_location))
        history = Store.get_talking_or_none(id)
        if history is None or len(history) < 10:
            return False
        
        history.reverse()
        last_history = history[0:10]
        print(last_history)
        
        num_changes = 0
        max_changes = len(last_history)-1
        for i in range(len(last_history)-1):
            if history[i] != last_history[i+1]:
                num_changes += 1
        
        res1 = num_changes / max_changes
        res2 = last_history.count(True) / len(last_history)
        
        print(["[TALKING PERCENTAG] ", str(0.3*res1 + 0.7*res2)])
        return (0.3*res1 + 0.7*res2) > TalkingIdentifier.threshold
        
