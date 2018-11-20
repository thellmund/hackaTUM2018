class Response:
    
    def __init__(self, timestamp, current):
        self.timestamp = timestamp
        self.current = current

    def __str__(self):
        return '{{"timestamp" = "{0}", "current" = {1}}}'\
            .format(self.timestamp, self.current)

    @classmethod
    def obj_creator(cls, d):
        return Response(d['timestamp'], d['current'])
