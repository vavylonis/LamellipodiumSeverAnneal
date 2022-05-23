# Read in and plot Fibroblast leading edge 
# simulation data to compare to Vinzenz et al. 

import csv
import matplotlib.pyplot as plt
import numpy as np 

BEdist = []
BEnum = []
BEdist2 = []
BEnum2 = []

EndDist = [0.125,0.375,0.625,0.875]

#data from Vinzenz et al. 
VBEnum = [586.257,173.513,86.5943,69.2049]

#Get the barbed and pointed end data 
with open('EndS_0_UnifS_5E-5_Ann_60_Lcrit_80\\BrBE.csv', 'r') as file:
    File = csv.reader(file)

    for row in File:
        if row[0] == '2':
            BEdist.append(float(row[1])) #distance from LE
            BE = float(row[2])
            BEnum.append(BE) #Barbed ends
 
#Get the barbed and pointed end data for data set 2
with open('EndS_1E-3_UnifS_5E-6_Ann_60_Lcrit_150\\BrBE.csv', 'r') as file:
    File = csv.reader(file)

    for row in File:
        if row[0] == '2':
            BEdist2.append(float(row[1])) #distance from LE
            BE2 = float(row[2])
            BEnum2.append(BE2) #Barbed ends divieded by bin size for number density
 
BESort = [x for _, x in sorted(zip(BEdist, BEnum))]
BEdist.sort()

BESort2 = [x for _, x in sorted(zip(BEdist2, BEnum2))]
BEdist2.sort()

BEDen = []
BEDen2 = []

interval = BEdist[1]-BEdist[0]
for i in range(0,len(BEdist),1):
    BEdist[i] = BEdist[i] - interval/2   
    BEDen.append(BESort[i]/0.25)  #divide bin size out for number density
for i in range(0,len(BEdist2),1):
    BEdist2[i] = BEdist2[i] - interval/2    
    BEDen2.append(BESort2[i]/0.25)  #divide bin size out for number density

BEBinC = []
BEBin2C = []
for i in range(0,len(BESort), 1):
    BEBinC.append(BESort[i]/(0.25*0.2*602.2))
for i in range(0,len(BESort2), 1):
    BEBin2C.append(BESort2[i]/(0.25*0.2*602.2))

ymax = 1200

#branch plot 
fig = plt.figure(figsize =(11, 9))  
fig.set_size_inches(10, 8) 
plt.subplots_adjust(left=0.18, bottom=0.15, right=0.9, top=None, wspace=None, hspace=None)
plt.rc('xtick', labelsize = 24)
plt.rc('ytick', labelsize = 24)
BE = plt.subplot(1,1,1)
BE4, = plt.plot(EndDist, VBEnum, '->g', linewidth = 5, markersize = 20) # Vinzenz et al. data 
BE1, = plt.plot(BEdist, BEDen,'-ok', markersize = 15, linewidth = 5, markevery = 4) # Branches for data 1
BE2, = plt.plot(BEdist2, BEDen2, '-sr', markersize = 15, linewidth = 5, markevery = 4) # Branhes for data 2
plt.ylim(0,ymax)
plt.xlim(0,10.2)
BE.set_title('Barbed Ends', fontsize = 40)
BE.set_xlabel('Distance from leading edge (\u03BCm)', fontsize = 28)  
BE.set_ylabel('Number density (\u03BCm$^{-2}$)', fontsize = 30)          
BE4.set_label('Vinzenz et al.')
BE1.set_label('Uniform severing')
BE2.set_label('Enhanced end severing')
plt.legend(fontsize = 24)
# add 2nd axis
ax2 = plt.twinx()  
ax2.set_ylabel('Barbed end concentration (\u03BCM)', fontsize = 28)  
ax2.plot(BEdist, BEBinC, color = "black", linewidth = 0.0) # BE for data 1
ax2.plot(BEdist2, BEBin2C, color = "red", linewidth = 0.0) # BE for data 2
ax2.plot(EndDist, [19.47,5.76,2.87,2.29], color = "green", linewidth = 0.0) # Vinzenz et al data in uM to set the 2nd axis
plt.ylim(0,ymax/(0.25*602.2))
plt.xticks([0,1,2,3,4,5,6,7,8,9,10], ['0','1','2','3','4','5','6','7','8','9','10']) 
plt.show()  


