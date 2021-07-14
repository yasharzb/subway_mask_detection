import random, sys


def mask_detector(file_path):
    ### Call your methods here ###
    return bool(random.getrandbits(1))


pic_path = sys.argv[1]

result = mask_detector(file_path=pic_path)
print(result)
