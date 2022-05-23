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

Bdist_tot = []
Bdist_UC = [] 
Bdist_rePoly = [] 
Bdist_depoly = [] 
Bdist_NC = [] 
Bdist_CUC = [] 

BESort_tot = []
BESort_UC = [] 
BESort_rePoly = [] 
BESort_depoly = [] 
BESort_NC = [] 
BESort_CUC = [] 

Bdist_tot1 = []
Bdist_UC1 = [] 
Bdist_rePoly1 = [] 
Bdist_depoly1 = [] 
Bdist_NC1 = [] 
Bdist_CUC1 = [] 

BE_tot = []
BE_UC = []
BE_rePoly = []
BE_depoly = []
BE_NC = []
BE_CUC = []


EndDist = [0.212,0.424,0.636,0.848,1.06,1.272,1.484,1.696,1.908,2.12]

#data from Mueller et al. 
#Note: first bin includes 0 to 424 nm, acts as a shift in the data by 2 bins.  0-212 nm only conatins 2.15 filaments
#Mueller et al data averaged of the two combined bins 
MBEnum = [1545.347625,1123.465,933.3725,883.9,812.559,797.4635,863.6255,729.7805,802.193,749.606]


#Get the barbed and pointed end data 
with open('BEDepoly\\Ends.csv', 'r') as file:
    BEPEFile = csv.reader(file)

    for row in BEPEFile:
        if row[0] == '2': #total BE number
            Bdist_tot1.append(float(row[1])) #distance from LE
            BE = float(row[2])
            BE_tot.append(BE/(1*0.106)) #toal barbed ends, read in as num/bin width of 0.106
        elif row[0] == '3': #total BE uncapped number (never capped + uncapped)
            Bdist_UC1.append(float(row[1])) #distance from LE
            BE = float(row[2])
            BE_UC.append(BE/(1*0.106)) #toal uncapped barbed ends, read in as num/bin width of 0.106
        elif row[0] == '9': #total re polymerizing BE
            Bdist_rePoly1.append(float(row[1])) #distance from LE
            BE = float(row[2])
            BE_rePoly.append(BE/(1*0.106)) #repolymerizing BE, read in as num/bin width of 0.106
        elif row[0] == '10': # depolymerizing BE
            Bdist_depoly1.append(float(row[1])) #distance from LE
            BE = float(row[2])
            BE_depoly.append(BE/(1*0.106)) #depolymerizing barbed ends, read in as num/bin width of 0.106
        elif row[0] == '11': # never capped BE
            Bdist_NC1.append(float(row[1])) #distance from LE
            BE = float(row[2])
            BE_NC.append(BE/(1*0.106)) #never capped, read in as num/bin width of 0.106
        elif row[0] == '12': #capped then uncapped BE (uncapped)
            Bdist_CUC1.append(float(row[1])) #distance from LE
            BE = float(row[2])
            BE_CUC.append(BE/(1*0.106)) #uncapped barbed ends that were capped, read in as num/bin width of 0.106

BESort_tot1 = [x for _, x in sorted(zip(Bdist_tot1, BE_tot))]
Bdist_tot1.sort()

BESort_UC1 = [x for _, x in sorted(zip(Bdist_UC1, BE_UC))]
Bdist_UC1.sort()

BESort_rePoly1 = [x for _, x in sorted(zip(Bdist_rePoly1, BE_rePoly))]
Bdist_rePoly1.sort()

BESort_depoly1 = [x for _, x in sorted(zip(Bdist_depoly1, BE_depoly))]
Bdist_depoly1.sort()

BESort_NC1 = [x for _, x in sorted(zip(Bdist_NC1, BE_NC))]
Bdist_NC1.sort()

BESort_CUC1 = [x for _, x in sorted(zip(Bdist_CUC1, BE_CUC))]
Bdist_CUC1.sort()

for i in range(int(len(BESort_tot1)/2)):
    if 2*i >= len(BESort_tot1):
        break
    BESort_tot.append((BESort_tot1[2*i] + BESort_tot1[2*i+1])/2) #add bins together then divide by two to average the number density (units /um^3), get average denisty in bins 0.212 um
    Bdist_tot.append(Bdist_tot1[2*i+1]/2) 
for i in range(int(len(BESort_UC1)/2)):
    if 2*i >= len(BESort_UC1):
        break
    BESort_UC.append((BESort_UC1[2*i] + BESort_UC1[2*i+1])/2) #add bins together then divide by two to average the number density (units /um^3), get average denisty in bins 0.212 um
    Bdist_UC.append(Bdist_UC1[2*i+1]/2) 
for i in range(int(len(BESort_rePoly1)/2)):
    if 2*i >= len(BESort_rePoly1):
        break
    BESort_rePoly.append((BESort_rePoly1[2*i] + BESort_rePoly1[2*i+1])/2) #add bins together then divide by two to average the number density (units /um^3), get average denisty in bins 0.212 um
    Bdist_rePoly.append(Bdist_rePoly1[2*i+1]/2)     
for i in range(int(len(BESort_depoly1)/2)):
    if 2*i >= len(BESort_depoly1):
        break
    BESort_depoly.append((BESort_depoly1[2*i] + BESort_depoly1[2*i+1])/2) #add bins together then divide by two to average the number density (units /um^3), get average denisty in bins 0.212 um
    Bdist_depoly.append(Bdist_depoly1[2*i+1]/2) 
for i in range(int(len(BESort_NC1)/2)):
    if 2*i >= len(BESort_NC1):
        break
    BESort_NC.append((BESort_NC1[2*i] + BESort_NC1[2*i+1])/2) #add bins together then divide by two to average the number density (units /um^3), get average denisty in bins 0.212 um
    Bdist_NC.append(Bdist_NC1[2*i+1]/2) 
for i in range(int(len(BESort_CUC1)/2)):
    if 2*i >= len(BESort_CUC1):
        break
    BESort_CUC.append((BESort_CUC1[2*i] + BESort_CUC1[2*i+1])/2) #add bins together then divide by two to average the number density (units /um^3), get average denisty in bins 0.212 um
    Bdist_CUC.append(Bdist_CUC1[2*i+1]/2)     
    
BE_tot_inC = []
BE_UC_inC = []
BE_rePoly_inC = []
BE_depoly_inC = []
BE_NC_inC = []
BE_CUC_inC = []

for i in range(0,len(BESort_tot), 1):
    BE_tot_inC.append(BESort_tot[i]/(0.212*602.2))  #convert to uM and elimate binsize 
for i in range(0,len(BESort_UC), 1):
    BE_UC_inC.append(BESort_UC[i]/(0.212*602.2))  #convert to uM and elimate binsize 
for i in range(0,len(BESort_rePoly), 1):
    BE_rePoly_inC.append(BESort_rePoly[i]/(0.212*602.2))  #convert to uM and elimate binsize
for i in range(0,len(BESort_depoly), 1):
    BE_depoly_inC.append(BESort_depoly[i]/(0.212*602.2))  #convert to uM and elimate binsize
for i in range(0,len(BESort_NC), 1):
    BE_NC_inC.append(BESort_NC[i]/(0.212*602.2))  #convert to uM and elimate binsize
for i in range(0,len(BESort_CUC), 1):
    BE_CUC_inC.append(BESort_CUC[i]/(0.212*602.2))  #convert to uM and elimate binsize

BE_cap = []
BE_cap_inC = []

for i in range(0,len(BESort_UC), 1):
    BE_cap.append(BESort_tot[i] - BESort_UC[i] - BESort_rePoly[i] - BESort_depoly[i] - BESort_NC[i] - BESort_CUC[i])
    BE_cap_inC.append(BE_tot_inC[i] - BE_UC_inC[i] - BE_rePoly_inC[i] - BE_depoly_inC[i] - BE_NC_inC[i] - BE_CUC_inC[i])

ymax = 2000
 
#barbed end plot   
plt.rc('xtick', labelsize = 24)
plt.rc('ytick', labelsize = 24)
fig = plt.figure(figsize =(11, 9))  
fig.set_size_inches(10, 8) 
plt.subplots_adjust(left=0.18, bottom=0.15, right=0.9, top=None, wspace=None, hspace=None)
BE = plt.subplot(1,1,1)
BE_data, = plt.plot(EndDist, MBEnum, '->g', linewidth = 5, markersize = 20) # Mueller et al. data 
BE2, = plt.plot(Bdist_tot, BE_cap, '-sr', markersize = 15, linewidth = 5, markevery = 4) # BE for data 2
BE3, = plt.plot(Bdist_rePoly, BESort_rePoly, '-^b', markersize = 15, linewidth = 5, markevery = 4) # BE for data 2
BE4, = plt.plot(Bdist_depoly, BESort_depoly, '-<c', markersize = 15, linewidth = 5, markevery = 4) # BE for data 2
BE5, = plt.plot(Bdist_tot, BESort_tot, '-ok', markersize = 15, linewidth = 5, markevery = 4) # BE for data 1
plt.ylim(0,ymax)
plt.xlim(0,10.2)
BE.set_title('Barbed Ends', fontsize = 40)
BE.set_xlabel('Distance from leading edge (\u03BCm)', fontsize = 28)  
BE.set_ylabel('Number density (\u03BCm$^{-2}$)', fontsize = 28)          
BE_data.set_label('Mueller et al.')
BE5.set_label('Total BE')
BE2.set_label('Stable BE')
BE3.set_label('Re-polymerizing BE')
BE4.set_label('Depolymerizing BE')
plt.legend(fontsize = 18)
plt.tick_params(length = 8, width = 3) 
ax2 = plt.twinx()  
ax2.set_ylabel('Barbed end concentration (\u03BCM)', fontsize = 28)  
ax2.plot(Bdist_NC, BE_NC_inC, color = "black", linewidth = 0.0) # BE for data 1
ax2.plot(Bdist_tot, BE_cap_inC, color = "red", linewidth = 0.0) # BE for data 2
ax2.plot(Bdist_rePoly, BE_rePoly_inC, color = "blue", linewidth = 0.0) # BE for data 1
ax2.plot(Bdist_depoly, BE_depoly_inC, color = "cyan", linewidth = 0.0) # BE for data 2
ax2.plot(Bdist_tot, BE_tot_inC, color = "black", linewidth = 0.0) # BE for data 2
plt.ylim(0,ymax/(0.212*602.2))
plt.xticks([0,1,2,3,4,5,6,7,8,9,10], ['0','1','2','3','4','5','6','7','8','9','10']) 
plt.show()