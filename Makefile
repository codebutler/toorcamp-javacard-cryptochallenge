JAVACARD_SDK_DIR = ../toorcamp-javacard-sdk

APPLET_AID      = 0xd0:0x70:0x02:0xca:0x44:0x90:0xcc:0x01
APPLET_NAME     = org.toorcamp.CryptoChallenge.CryptoChallenge 
PACKAGE_AID     = 0xd0:0x70:0x02:0xCA:0x44:0x90:0xcc
PACKAGE_NAME    = org.toorcamp.CryptoChallenge
PACKAGE_VERSION = 1.0

SOURCES = \
	src/org/toorcamp/CryptoChallenge/CryptoChallenge.java

include ../toorcamp-javacard-sdk/makefiles/applet-project.mk
