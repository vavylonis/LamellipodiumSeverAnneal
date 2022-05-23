# Read in and plot concentration, barbed and pointed end concentration and cummulative length distribution  
# Compare two sets of data

import csv
import matplotlib.pyplot as plt
import numpy as np 

Brdist = []
Branch = []

#for the second set of data
Brdist2 = []
Branch2 = []

#for the third set of data
Brdist3 = []
Branch3 = []
      
#Get the branch concentration profile data
with open('Keratocyte_WithEndSev_Ann\\Ends.csv', 'r') as file:
    endsFile = csv.reader(file)
    next(endsFile)

    for row in endsFile:
        if row[0] == '1':
            Brdist.append(float(row[1])) #distance from LE
            Branch.append(float(row[2])) #Branch concentration

#Get the branch concentration profile data of data set 2
with open('Keratocyte_NoEndSev_Ann\\Ends.csv', 'r') as file:
    endsFile2 = csv.reader(file)
    next(endsFile2)

    for row in endsFile2:
        if row[0] == '1':
            Brdist2.append(float(row[1])) #distance from LE
            Branch2.append(float(row[2])) #Branch concentration       


BrSort = [x for _, x in sorted(zip(Brdist, Branch))]
Brdist.sort()

BrSort2 = [x for _, x in sorted(zip(Brdist2, Branch2))]
Brdist2.sort()

BranchnC = []
BranchnC2 = []
BranchN = []
BranchN2 = []

for i in range(0,len(BrSort), 1):
    BranchN.append(BrSort[i]/(0.25))
    BranchnC.append(BrSort[i]/(0.2*0.25*1*602.2))
for i in range(0,len(BrSort2), 1):
    BranchN2.append(BrSort2[i]/(0.25))
    BranchnC2.append(BrSort2[i]/(0.2*0.25*1*602.2))

ymax = 1000

#Branch plot 
plt.figure(1)
fig = plt.figure(figsize =(11, 9))  
fig.set_size_inches(10, 8) 
plt.subplots_adjust(left=0.18, bottom=0.15, right=0.9, top=None, wspace=None, hspace=None)
plt.rc('xtick', labelsize = 24)
plt.rc('ytick', labelsize = 24)
plt.subplots_adjust(left=None, bottom=0.15, right=None, top=None, wspace=None, hspace=None)

branch = plt.subplot(1,1,1)
Sim, = plt.plot(Brdist, BranchN, '-ok', markersize = 15, linewidth = 5, markevery = 4) # Branch concentration
Sim2, = plt.plot(Brdist2, BranchN2, '-sr', markersize = 15, linewidth = 5, markevery = 4) 

plt.ylim(0,ymax)
plt.xlim(0,10.2)
branch.set_xlabel('Distance from leading edge (\u03BCm)', fontsize = 28)  ###################################
branch.set_ylabel('Number density (\u03BCm$^{-2}$)', fontsize = 28)  
branch.set_title('Branches', fontsize = 40)                 
Sim.set_label('Without enhanced end severing')
Sim2.set_label('With enhanced end severing')

plt.legend(fontsize = 18)
plt.tick_params(length = 8, width = 3) 
# add 2nd axis
ax2 = plt.twinx()  
ax2.set_ylabel('Branch concentration (\u03BCM)', fontsize = 28)  
ax2.plot(Brdist, BranchnC, color = "black", linewidth = 0.00) # BE for data 1
ax2.plot(Brdist2, BranchnC2, color = "red", linewidth = 0.00) # BE for data 2
plt.ylim(0,ymax/(0.212*602.2))
plt.xticks([0,1,2,3,4,5,6,7,8,9,10], ['0','1','2','3','4','5','6','7','8','9','10']) 
plt.show()

