# Read in and plot concentration, barbed and pointed end concentration and cummulative length distribution  

import csv
import matplotlib.pyplot as plt
import numpy as np 

nearLE = []
nPercent = []
awayLE = []
aPercent = []

nearLE2 = []
nPercent2 = []
awayLE2 = []
aPercent2 = []

nearLE3 = []
nPercent3 = []
awayLE3 = []
aPercent3 = []

#Get the filament length data 
with open('Keratocyte_NoEndSev_Ann\\Length_3-4.csv', 'r') as file:
    LenFile = csv.reader(file)

    for row in LenFile:
        if row[0] == '2':
            nearLE.append(float(row[2])) #filament length 0-1 um from LE 
        elif row[0] == '3':
            awayLE.append(float(row[2])) #filament length 3-4 um from LE

#Get the filament length data for data set 2
with open('Keratocyte_WithEndSev_Ann\\Length_3-4.csv', 'r') as file:
    LenFile = csv.reader(file)

    for row in LenFile:
        if row[0] == '2':
            nearLE2.append(float(row[2])) #filament length 0-1 um from LE 
        elif row[0] == '3':
            awayLE2.append(float(row[2])) #filament length 3-4 um from LE

 # % in filaments 
nearLE.sort()
awayLE.sort()

for i in range(len(nearLE)): 
    nPercent.append(float(100*i/len(nearLE))) 
    
for j in range(len(awayLE)):
    aPercent.append(float(100*j/len(awayLE)))


nearLE2.sort()
awayLE2.sort()

for i in range(len(nearLE2)): 
    nPercent2.append(float(100*i/len(nearLE2))) 
    
for j in range(len(awayLE2)):
    aPercent2.append(float(100*j/len(awayLE2)))

#Cumulative length distribution
plt.figure(1)
fig = plt.figure(figsize =(11, 9))  
fig.set_size_inches(10, 8) 
plt.subplots_adjust(left=0.18, bottom=0.15, right=0.9, top=None, wspace=None, hspace=None)
plt.rc('xtick', labelsize = 24)
plt.rc('ytick', labelsize = 24)
plt.subplots_adjust(left=None, bottom=0.15, right=None, top=None, wspace=None, hspace=None)
len = plt.subplot(1,1,1)
near, = plt.plot(nearLE, nPercent, "k", linewidth = 5) # F-actin concentration plo
away, = plt.plot(awayLE, aPercent, "--k", linewidth = 5) # O-actin concentration plot
near2, = plt.plot(nearLE2, nPercent2, "r", linewidth = 5) # F-actin concentration plo
away2, = plt.plot(awayLE2, aPercent2, "--r", linewidth = 5) # O-actin concentration plot
plt.xlim(0,1000)
len.set_xlabel('Filament length (\u03BCm)', fontsize = 30)  ###################################
len.set_ylabel('Percent', fontsize = 30)       
len.set_title('Cumulative Length Distribution', fontsize = 40)            
near.set_label('Near LE, 0-1 \u03BCm')
away.set_label('Away LE, 3-4 \u03BCm')
plt.legend(fontsize = 24)
plt.xticks([0,100,200,300,400,500,600,700,800,900,1000], ['0','100','200','300','400','500','600','700','800','900 ',' 1000']) 
plt.tick_params(length = 8, width = 3) 
plt.show()
