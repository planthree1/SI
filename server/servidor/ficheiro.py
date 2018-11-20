from Crypto import Random
from Crypto.PublicKey import RSA
import base64

def generate_keys():
    # RSA modulus length must be a multiple of 256 and >= 1024
    modulus_length = 256*4 # use larger value in production
    privatekey = RSA.generate(modulus_length, Random.new().read)
    publickey = privatekey.publickey()

    print (privatekey)
    print (publickey)
    return privatekey, publickey

generate_keys()