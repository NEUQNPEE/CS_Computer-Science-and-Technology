	.org 0x0
	.global _start
	.set noat
_start:
	addi  $1,$0,0x1234     	#$1=0x00001234
	addi  $2,$0,0x5678		#$2=0x00005678
	sub   $3,$1,$2			#$3=0xFFFFBBBC
	slti  $4,$3,0x1			#$4=0x00000001
	sltiu $5,$3,0x1         #$5=0x00000000
	andi  $6,$1,0x5678		#$6=0x00001230
	ori   $7,$1,0x5678      #$7=0x0000567c
	xori  $8,$1,0x5678      #$8=0x0000444c
	sllv  $9,$2,$1          #$9=0x67800000
	addi  $31,$0,0x2
	srav  $10,$3,$31        #$10=0xFFFFFBBB
	srlv  $11,$3,$31		#$11=0x0FFFFBBB
	mult  $1,$3				
	mfhi  $12				#$12=0xFFFFFFFF
	mflo  $13				#$13=0xFB255a30
	multu $1,$3				
	mfhi  $14				#$14=0x00001233
	mflo  $15				#$15=0xFB255a30
	div   $zero,$3,$1		
	mfhi  $16				#$16=0xFFFFF258
	mflo  $17				#$17=0xFFFFFFFD
	divu  $3,$1				
	mfhi  $18				#$18=0x000000F0
	mflo  $19				#$19=0x000E103F
	mthi  $1				#hi=0x00001234
	mtlo  $2				#lo=0x00005678
	