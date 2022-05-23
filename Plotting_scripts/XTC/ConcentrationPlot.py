# Read in and plot concentration

import csv
import matplotlib.pyplot as plt
import numpy as np 

Fdist = []
Factin = []
Odist = []
Oactin = []
OactShort = []
OdistShort = []

#for the second set of data
Fdist2 = []
Factin2 = []
Odist2 = []
Oactin2 = []
OactShort2 = []
OdistShort2 = []

#for the third set of data
Fdist3 = []
Factin3 = []
Odist3 = []
Oactin3 = []
OactShort3 = []
OdistShort3 = []


#Get the concentration profile data
with open('XTC_NoEndSev_NoAnn\\Concentration.csv', 'r') as file:
    concFile = csv.reader(file)
    next(concFile)

    for row in concFile:
        if row[0] == '1':
            Fdist.append(float(row[1])) #distance from LE
            Factin.append(float(row[2])) #F-actin concentration
        elif row[0] == '2':
            Odist.append(float(row[1])) #distance from LE
            Oactin.append(float(row[2])) #O-actin concentration
        
#Get the concentration profile of data 2
with open('XTC_WithEndSev_NoAnn\\Concentration.csv', 'r') as file:
    concFile2 = csv.reader(file)
    next(concFile2)

    for row in concFile2:
        if row[0] == '1':
            Fdist2.append(float(row[1])) #distance from LE
            Factin2.append(float(row[2])) #F-actin concentration
        elif row[0] == '2':
            Odist2.append(float(row[1])) #distance from LE
            Oactin2.append(float(row[2])) #O-actin concentration
     
#sort so we can plot a line using plot
FactSort = [x for _, x in sorted(zip(Fdist, Factin))]
Fdist.sort()

OactSort = [x for _, x in sorted(zip(Odist, Oactin))]
Odist.sort()
for i in range(len(Odist)):
    if Odist[i] <= 10.0:
        OdistShort.append(Odist[i])
        OactShort.append(OactSort[i])

#sort data 2 so we can plot a line using plot
FactSort2 = [x for _, x in sorted(zip(Fdist2, Factin2))]
Fdist2.sort()

OactSort2 = [x for _, x in sorted(zip(Odist2, Oactin2))]
Odist2.sort()
for i in range(len(Odist2)):
    if Odist2[i] <= 10.0:
        OdistShort2.append(Odist2[i])
        OactShort2.append(OactSort2[i])

#Concentration plot     
fig = plt.figure(1, figsize =(11, 9))  
fig.set_size_inches(10, 8) 
plt.subplots_adjust(left=0.18, bottom=0.15, right=0.9, top=None, wspace=None, hspace=None)
plt.rc('xtick', labelsize = 24)
plt.rc('ytick', labelsize = 24)
conc = plt.subplot(1,1,1)
F, = plt.plot(Fdist, FactSort,'-ok', markersize = 15, linewidth = 5, markevery = 4) # F-actin concentration plot
F2, = plt.plot(Fdist2, FactSort2, '-sr', markersize = 15, linewidth = 5, markevery = 4) # F-actin concentration plot
plt.ylim(0,800)
plt.xlim(0,10)
conc.set_xlabel('Distance from leading edge (\u03BCm)', fontsize = 30)  ###################################
conc.set_ylabel('Concentration (\u03BCM)', fontsize = 30)          
conc.set_title('F-actin Concentration', fontsize = 40)                 
F.set_label('Uniform severing')
F2.set_label('Enhanced end severing')
plt.legend(fontsize = 24)
plt.tick_params(length = 8, width = 3) 
plt.xticks([0,1,2,3,4,5,6,7,8,9,10], ['0','1','2','3','4','5','6','7','8','9','10']) 
plt.show()  
