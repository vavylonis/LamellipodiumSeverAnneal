# Read in and plot keratocyte leading edge 
# simulation data to compare to Mueller et al. 

import csv
import matplotlib.pyplot as plt
import numpy as np 

# barbed end, pointed end, filament number 

Bdist = []
BEnum = []
BEBinDist = []
BEBin = []
Bdist2 = []
BEnum2 = []
BEBinDist2 = []
BEBin2 = []
Bdist3 = []
BEnum3 = []
BEBinDist3 = []
BEBin3 = []

EndDist = [0.212,0.424,0.636,0.848,1.06,1.272,1.484,1.696,1.908,2.12]

# keratocyte - Mueller et al data averag of  two combined bins 
MBEnum = [1545.347625,1123.465,933.3725,883.9,812.559,797.4635,863.6255,729.7805,802.193,749.606]

#Get the barbed and pointed end data 
with open('Keratocyte_NoEndSev_Ann\\BEPEFilN.csv', 'r') as file:
    BEPEFile = csv.reader(file)

    for row in BEPEFile:
        if row[0] == '1':
            Bdist.append(float(row[1])) #distance from LE
            BE = float(row[2])
            BEnum.append(BE) #Barbed ends
 
#Get the barbed and pointed end data for data set 2
with open('Keratocyte_WithEndSev_Ann\\BEPEFilN.csv', 'r') as file:
    BEPEFile = csv.reader(file)

    for row in BEPEFile:
        if row[0] == '1':
            Bdist2.append(float(row[1])) #distance from LE
            BE2 = float(row[2])
            BEnum2.append(BE2) #Barbed ends

BESort = [x for _, x in sorted(zip(Bdist, BEnum))]
Bdist.sort()

BESort2 = [x for _, x in sorted(zip(Bdist2, BEnum2))]
Bdist2.sort()

for i in range(int(len(BESort)/2)):
    if 2*i >= len(BESort):
        break
    BEBin.append((BESort[2*i] + BESort[2*i+1])/2) #add bins together then divide by two to average the number density (units /um^3), get average denisty in bins 0.212 um
    BEBinDist.append(Bdist[2*i+1]/2) 

for i in range(int(len(BESort2)/2)):
    if 2*i >= len(BESort2):
        break
    BEBin2.append((BESort2[2*i] + BESort2[2*i+1])/2)
    BEBinDist2.append(Bdist2[2*i+1]/2)

width = 0.08
EndDistP = []
FilDistP = []
for i in range(0, len(EndDist),1):
    EndDistP.append(EndDist[i] + width)

BEBinC = []
BEBin2C = []
for i in range(0,len(BEBin), 1):
    BEBinC.append(BEBin[i]/(0.2*602.2))
for i in range(0,len(BEBin2), 1):
    BEBin2C.append(BEBin2[i]/(0.2*602.2))

ymax = 2000

#barbed end plot   
fig = plt.figure(figsize =(11, 9))  
fig.set_size_inches(10, 8) 
plt.subplots_adjust(left=0.18, bottom=0.15, right=0.9, top=None, wspace=None, hspace=None)
plt.rc('xtick', labelsize = 24)
plt.rc('ytick', labelsize = 24)
plt.subplots_adjust(left=None, bottom=0.15, right=None, top=None, wspace=None, hspace=None)
BE = plt.subplot(1,1,1)
BE3, = plt.plot(EndDist, MBEnum, '->g', linewidth = 5, markersize = 20) # Mueller et al. data 
BE1, = plt.plot(BEBinDist, BEBin, '-ok', markersize = 15, linewidth = 5, markevery = 4) # BE for data 1
BE2, = plt.plot(BEBinDist2, BEBin2, '-sr', markersize = 15, linewidth = 5, markevery = 4) # BE for data 2
plt.ylim(0,ymax)
plt.xlim(0,10.2)
BE.set_title('Barbed Ends', fontsize = 40)
BE.set_xlabel('Distance from leading edge (\u03BCm)', fontsize = 28)  
BE.set_ylabel('Number density (\u03BCm$^{-2}$)', fontsize = 28)          
BE3.set_label('Mueller et al.')
BE1.set_label('Uniform severing')
BE2.set_label('Enhanced end severing')
plt.legend(fontsize = 24)
plt.tick_params(length = 8, width = 3) 
# add 2nd axis
ax2 = plt.twinx()  
ax2.set_ylabel('Barbed end concentration (\u03BCM)', fontsize = 28)  
ax2.plot(BEBinDist, BEBinC, color = "black", linewidth = 0.00) # BE for data 1
ax2.plot(BEBinDist2, BEBin2C, color = "red", linewidth = 0.00) # BE for data 2
plt.ylim(0,ymax/(0.212*602.2))
plt.xticks([0,1,2,3,4,5,6,7,8,9,10], ['0','1','2','3','4','5','6','7','8','9','10'])
plt.yticks([0,2,4,6,8,12,10,14,16], ['0','2','4','6','8','10','12','14','16']) 
plt.show()  

