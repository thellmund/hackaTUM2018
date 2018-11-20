import base64

class Request:
    
    def __init__(self, timestamp, requires_thumbnails, image_data):
        self.timestamp = timestamp
        self.requires_thumbnails = requires_thumbnails
        self.image_data = image_data

    def __str__(self):
        return '{{"timestamp" = "{0}","requires_thumbnails" = "{1}", "image_data" = {2}}}'\
            .format(self.timestamp, self.requires_thumbnails, self.image_data)

    @classmethod
    def obj_creator(cls, d):
        return Request(d['timestamp'], d['requires_thumbnails'], d['image_data'])

    def store_image_bitmap(self):
        filename = str(self.timestamp) + '.png'
        with open(filename, 'wb') as file:
            file.write(base64.decodebytes(self.image_data.encode('utf-8')))
