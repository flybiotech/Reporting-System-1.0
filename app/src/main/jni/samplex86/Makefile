#-----------------------------------------------------------------------------
# Author      : ÷Ï∫Ï≤®
# Date        : 2013.04.20
# Version     : V 2.00
# Description :
#-----------------------------------------------------------------------------

export CORSS_COMPILE=
export P2PPATH=libtutk_15W13_x86

#export CORSS_COMPILE=/zhb/armgcc/4.4.0/bin/arm-linux-
#export P2PPATH=arm440

#export CORSS_COMPILE=/zhb/armgcc/4.4.0/bin/arm-linux-
#export P2PPATH=arm440

export CC=$(CORSS_COMPILE)gcc
export AR=$(CORSS_COMPILE)ar
export STRIP=$(CORSS_COMPILE)strip
export LD=$(CORSS_COMPILE)ld

LibName = thSDK
ExeName = demo
Paramlib = -s -lpthread -lm -l$(LibName) -L.
Paramlibex =  -lAVApis -lIOTCAPIs -L$(P2PPATH)

all:
	$(MAKE) -C ../linuxthSDK
	cp -rf ../linuxthSDK/libthSDK.a .
	$(CC)  -o searchdev searchdev.c $(Paramlib) $(Paramlibex)
	$(CC)  -o playp2ptalk playp2ptalk.c $(Paramlib) $(Paramlibex)
	$(CC)  -o playlive playlive.c $(Paramlib) $(Paramlibex)
	$(CC)  -o playremote playremote.c $(Paramlib) $(Paramlibex)
	$(CC)  -o playlivep2p playlivep2p.c $(Paramlib) $(Paramlibex)
	$(CC)  -o playremotep2p playremotep2p.c $(Paramlib) $(Paramlibex)

clean: 
	$(MAKE) -C ../linuxthSDK clean
	rm -f *.a $(ExeName) searchdev playp2ptalk playlive playremote playlivep2p playremotep2p


	
