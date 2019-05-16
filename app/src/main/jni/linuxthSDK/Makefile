#-----------------------------------------------------------------------------
# Author      : ÷Ï∫Ï≤®
# Date        : 2013.04.20
# Version     : V 2.00
# Description :
#-----------------------------------------------------------------------------
#export CORSS_COMPILE=arm-linux-

export CC=$(CORSS_COMPILE)gcc
export AR=$(CORSS_COMPILE)ar
export STRIP=$(CORSS_COMPILE)strip
export LD=$(CORSS_COMPILE)ld

Filelib= \
skt.o list.o \
thSDKlib.o \
common.o

LibName = thSDK
ExeName = demo
Paramlib = -s -lpthread -lm -l$(LibName) -L.

all: 
	make lib
	
lib: $(Filelib)
	$(AR) -cvr lib$(LibName).a $(Filelib)
#	cp lib$(LibName).a ../

dll: $(Filelib)
	$(CC) -shared -o lib$(LibName).so $(Filelib)	
	$(STRIP) -s lib$(LibName).so
#	cp lib$(LibName).so ../
	rm -rf *.o
	
clean: 
	rm -f *.a


	
