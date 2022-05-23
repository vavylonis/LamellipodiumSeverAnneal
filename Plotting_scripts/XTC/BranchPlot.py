# Read in and plot Fibroblast leading edge 
# simulation data to compare to Vinzenz et al. 

import csv
import matplotlib.pyplot as plt
import numpy as np 

brDist = []
brNum = []
brDist2 = []
brNum2 = []

VbrDist =  [0.125,0.375,0.625,0.875]

#data from Vinzenz et al. 
VbrNum = [149.79,152.432,140.787,94.3552]

#Get the branch data 
with open('XTC_NoEndSev_NoAnn\\BrBE.csv', 'r') as file:
    File = csv.reader(file)

    for row in File:
        if row[0] == '1':
            brDist.append(float(row[1])) #distance from LE
            brNum.append(float(row[2])) #number of branches
 
#Get the branch data 2 
with open('XTC_WithEndSev_NoAnn\\BrBE.csv', 'r') as file:
    File = csv.reader(file)

    for row in File:
        if row[0] == '1':
            brDist2.append(float(row[1])) #distance from LE
            brNum2.append(float(row[2])) #number of branches

brSort = [x for _, x in sorted(zip(brDist, brNum))]
brDist.sort()

brSort2 = [x for _, x in sorted(zip(brDist2, brNum2))]
brDist2.sort()

interval = brDist[1]-brDist[0]
for i in range(0,len(brDist),1):
    brDist[i] = brDist[i] - interval/2
for i in range(0,len(brDist2),1):
    brDist2[i] = brDist2[i] - interval/2

BranchnC = []
BranchnC2 = []
BranchN = []
BranchN2 = []

for i in range(0,len(brSort), 1):
    BranchN.append(brSort[i]/(0.25))
    BranchnC.append(brSort[i]/(0.2*0.25*1*602.2))
for i in range(0,len(brSort2), 1):
    BranchN2.append(brSort2[i]/(0.25))
    BranchnC2.append(brSort2[i]/(0.2*0.25*1*602.2))

ymax = 600

#branch plot 
fig = plt.figure(1, figsize =(11, 9))  
fig.set_size_inches(10, 8) 
plt.subplots_adjust(left=0.18, bottom=0.15, right=0.9, top=None, wspace=None, hspace=None)
plt.rc('xtick', labelsize = 24)
plt.rc('ytick', labelsize = 24)
plt.subplots_adjust(left=None, bottom=0.15, right=None, top=None, wspace=None, hspace=None)
br = plt.subplot(1,1,1)
#br4, = plt.plot(VbrDist, VbrNum, color = "green", linewidth = 5) # Vinzenz et al. data 
br4, = plt.plot(VbrDist, VbrNum, '->g', linewidth = 5, markersize = 20) # Vinzenz et al. data 
br1, = plt.plot(brDist, BranchN,'-ok', markersize = 15, linewidth = 5, markevery = 4) # Branches for data 1
br2, = plt.plot(brDist2, BranchN2, '-sr', markersize = 15, linewidth = 5, markevery = 4) # Branhes for data 2
plt.ylim(0,ymax)
plt.xlim(0,10.2)
br.set_title('Branches', fontsize = 40)
br.set_xlabel('Distance from leading edge (\u03BCm)', fontsize = 28)  
br.set_ylabel('Number density (\u03BCm$^{-2}$)', fontsize = 28)          
br4.set_label('Vinzenz et al.')
br1.set_label('Without enhanced end severing')
br2.set_label('With enhanced end severing')
plt.legend(fontsize = 18)
# add 2nd axis
ax2 = plt.twinx()  
ax2.set_ylabel('Branch concentration (\u03BCM)', fontsize = 28)  
ax2.plot(brDist, BranchnC, color = "black", linewidth = 0.0) # BE for data 1
ax2.plot(brDist2, BranchnC2, color = "red", linewidth = 0.0) # BE for data 2
plt.ylim(0,ymax/(0.25*602.2))
plt.xticks([0,1,2,3,4,5,6,7,8,9,10], ['0','1','2','3','4','5','6','7','8','9','10']) 
plt.show()  

