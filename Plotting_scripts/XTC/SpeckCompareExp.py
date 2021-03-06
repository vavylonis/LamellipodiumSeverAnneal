# Read in speckle csv file to plot lifetime and appearance/disapearance profiles

import csv
import matplotlib.pyplot as plt
import numpy as np


ti = float(230.0)
tf = float(250.0)

binSt = 4  #bin sizes for probability density
binS = 0.5

nb = 11 # number of bins for normalization, graphing
nbt = 36

time_f = []
time_i = []
z_i = []
z_f = []
delta_t = []
delta_z = []
delta_x = []
delta_y = []
annDist = []
reanneal = []
type = []
reApp = []

time_f2 = []
time_i2 = []
z_i2 = []
z_f2 = []
delta_t2 = []
delta_z2 = []
delta_x2 = []
delta_y2 = []
annDist2 = []
reanneal2 = []
type2 = []
reApp2 = []

nbins = 24 # linspace starts at zero and ends at the max value 
max = 12
step = max/(nbins)

nbinst = 36
maxt = 144
stept = maxt/(nbinst)

sum_time = 0
sum_zi = 0 
sum_zf = 0 

sum_time2 = 0
sum_zi2 = 0 
sum_zf2 = 0 

#lifetime data taken from laura's graphs
expTimeBin = [2,6,10,14,18,22,26,30,34,38,42,46,50,54,58,62,66,70,74,78,82,86,90,94,98,102,106,110,114,118,122,126,130,134,138,142]
#correcting for photobleaching
expSpeckPerc = [13.76317696,18.89429996,12.14384662,11.12049757,6.507017421,3.175461409,3.007161417,2.92380645,2.754968973,0.962301955,2.418100246,2.335529113,2.081224801,1.997869835,1.316161927,0.891156857,0.038757596,0.724977691,0.727631523,0.815501363,0,1.076450333,0,1.251921271,0.485266097,0.316697362,0.660737035,0.834082668,0.153171805,0.069285624,1.012473941,1.01486351,0.076718594,1.275545976,0.166975987,3.075645964]
expTimeBin_0to2 = 2 #actualy 0 to 4s
expSpeckPerc_0to2 = 13.76317696

#appearance data taken from laura's graphs
expAppBinL = [0.25,0.75,1.25,1.75,2.25,2.75,3.25,3.75,4.25,4.75,5.25,5.75]
expAppPercL = [0.15,0.20,0.02,0.05,0.03,0.01,0.02,0,0.02,0,0,0]

#appearance data extimated from Watanabe and Mitchison 2002 speckle images, normalized
expAppBinY = [0.332334,1.81891,2.74447,4.06041,5.26344,6.588,7.84589,8.83109,10.0277]
expAppFracY = [0.364698292,0.128653538,0.101402308,0.08058302,0.059650626,0.09923165,0.080367424,0.04915013,0.036263012] #normalized


with open('XTC_NoEndSev_NoAnn\\Speckles.csv', 'r') as file:
    speckFile = csv.reader(file)

 #   header = speckFile.next(speckFile) #skip first line/header of CSV file
    next(speckFile)

    for row in speckFile:
        if row[0] == 'Time':
            continue
        elif ti <= float(row[0]) and tf > float(row[0]): #only look at a portion of all the events  
            time_f.append(float(row[0])) #time -- db
            #speckle_id -- int
            #dist_from_PE -- int
            time_i.append(float(row[3])) #init_spec_time -- db
            #Fil_ID -- int
            #init_x -- db
            #init_y -- db
            z_i.append(float(row[7])) #init_z -- db
            #fin_x -- db
            #fin_y -- db
            z_f.append(float(row[10])) #fin_z -- db
            #type -- int
            t = float(row[0]) - float(row[3]) 
            delta_t.append(t)
            z = float(row[10]) - float(row[7])
            delta_z.append(z)
            x = float(row[8]) - float(row[5])
            delta_x.append(x)
            y = float(row[9]) - float(row[6])
            delta_y.append(y)
            dist = np.sqrt(x*x + y*y + z*z)
            annDist.append(dist)
            type.append(float(row[11]))
            #if t <= 2:# and z <= 0.2:  # how far it would move backward with retrograde flow
            #    reanneal.append(True) # speckle reannealed
            #else: 
            #    reanneal.append(False) # speckle did not reanneal

            
#read in file for no annealing/leading edge model
with open('XTC_WithEndSev_NoAnn\\Speckles.csv', 'r') as file:
    speckFile = csv.reader(file)

 #   header = speckFile.next(speckFile) #skip first line/header of CSV file
    next(speckFile)

    for row in speckFile:
        if row[0] == 'Time':
            continue
        elif ti <= float(row[0]) and tf > float(row[0]): #only look at a portion of all the events  
            time_f2.append(float(row[0])) #time -- db
            #speckle_id -- int
            #dist_from_PE -- int
            time_i2.append(float(row[3])) #init_spec_time -- db
            #Fil_ID -- int
            #init_x -- db
            #init_y -- db
            z_i2.append(float(row[7])) #init_z -- db
            #fin_x -- db
            #fin_y -- db
            z_f2.append(float(row[10])) #fin_z -- db
            #type -- int
            t = float(row[0]) - float(row[3]) 
            delta_t2.append(t)
            z = float(row[10]) - float(row[7])
            delta_z2.append(z)
            x = float(row[8]) - float(row[5])
            delta_x2.append(x)
            y = float(row[9]) - float(row[6])
            delta_y2.append(y)
            dist = np.sqrt(x*x + y*y + z*z)
            annDist2.append(dist)
            type2.append(float(row[11]))
            if t <= 2:# and z <= 0.2:  # how far it would move backward with retrograde flow
                reanneal2.append(True) # speckle reannealed
            else: 
                reanneal2.append(False) # speckle did not reanneal
                
                
counts_time = np.zeros(nbinst,dtype = int)
counts_zi = np.zeros(nbins,dtype = int)
counts_zf = np.zeros(nbins,dtype = int)
binst = np.zeros(nbinst, dtype = float)
bins = np.zeros(nbins, dtype = float)

counts_time2 = np.zeros(nbinst,dtype = int)
counts_zi2 = np.zeros(nbins,dtype = int)
counts_zf2 = np.zeros(nbins,dtype = int)
binst2 = np.zeros(nbinst, dtype = float)
bins2 = np.zeros(nbins, dtype = float)

n0_1s = 0
n1_2s = 0
for i in range(0,len(delta_t),1):
    if z_i[i] <= 12 and z_f[i] <= 12: #exclude anything that's outside 12 um 
    
        if 0 <= delta_t[i] and 1 > delta_t[i]:
            n0_1s = n0_1s + 1
        elif 1 <= delta_t[i] and 2 > delta_t[i]:
            n1_2s = n1_2s + 1    
        for j in range(0,nbinst,1): 
            minj = j*stept
            maxj = (j+1)*stept
            if minj <= delta_t[i] and maxj > delta_t[i]:
                counts_time[j] = counts_time[j] + 1
                if delta_t[i] > 4 and delta_t[i] < 144:
                    sum_time = sum_time + 1
                break

for i in range(0,len(z_i),1):
    for j in range(0,nbins,1): 
        minj = j*step
        maxj = (j+1)*step
        if minj <= z_i[i] and maxj > z_i[i] and delta_t[i] > 4 and delta_t[i] < 144:
            sum_zi = sum_zi + 1 
            counts_zi[j] = counts_zi[j] + 1
            break    

for i in range(0,len(z_f),1):
    for j in range(0,nbins,1): 
        minj = j*step
        maxj = (j+1)*step
        if minj <= z_f[i] and maxj > z_f[i] and delta_t[i] > 4 and delta_t[i] < 144:
            sum_zf = sum_zf + 1 
            counts_zf[j] = counts_zf[j] + 1
            break

t_tot = sum(counts_time[0:nbt])
norm_time = counts_time/(t_tot*binSt)
zi_tot = sum(counts_zi[0:nb])
norm_zi = counts_zi/(zi_tot*binS)
zf_tot = sum(counts_zf[0:nb])
norm_zf = counts_zf/(zf_tot*binS)

#for set 2 without annealing
n0_1s2 = 0
n1_2s2 = 0
for i in range(0,len(delta_t2),1):
    if z_i2[i] <= 12 and z_f2[i] <= 12:
        if 0 <= delta_t2[i] and 1 > delta_t2[i]:
            n0_1s2 = n0_1s2 + 1
        elif 1 <= delta_t2[i] and 2 > delta_t2[i]:
            n1_2s2 = n1_2s2 + 1
        for j in range(0,nbinst,1): 
            minj = j*stept
            maxj = (j+1)*stept
            if minj <= delta_t2[i] and maxj > delta_t2[i]:
                counts_time2[j] = counts_time2[j] + 1
                if delta_t2[i] > 4 and delta_t2[i] < 144:
                    sum_time2 = sum_time2 + 1
                break

for i in range(0,len(z_i2),1):
    for j in range(0,nbins,1): 
        minj = j*step
        maxj = (j+1)*step
        if minj <= z_i2[i] and maxj > z_i2[i] and delta_t2[i] > 4 and delta_t2[i] < 144:
            sum_zi2 = sum_zi2 + 1 
            counts_zi2[j] = counts_zi2[j] + 1
            break    

for i in range(0,len(z_f2),1):
    for j in range(0,nbins,1): 
        minj = j*step
        maxj = (j+1)*step
        if minj <= z_f2[i] and maxj > z_f2[i] and delta_t2[i] > 4 and delta_t2[i] < 144:
            sum_zf2 = sum_zf2 + 1 
            counts_zf2[j] = counts_zf2[j] + 1
            break

t_tot2 = sum(counts_time2[0:nbt])
norm_time2 = counts_time2/(t_tot2*binSt)
zi_tot2 = sum(counts_zi2[0:nb])
norm_zi2 = counts_zi2/(zi_tot2*binS)
zf_tot2 = sum(counts_zf2[0:nb])
norm_zf2 = counts_zf2/(zf_tot2*binS)

#set up values for the bins for the x-axis
for i in range(0,nbinst,1):
    binst[i] = i*stept
        
for i in range(0,nbins,1):
    bins[i] = i*step

labels = ['Watanabe & Mitchison','Uniform severing', 'Enhanced end severing']

expTimeBinShift = []
expTimeBinShift.append(float(1.0))
expSpeckFrac = []
expSpeckFrac.append(float(0.1376318/2)) #for probability density
for i in range(1,len(expTimeBin),1):
    expTimeBinShift.append(float(expTimeBin[i]))# + 0.75))
    expSpeckFrac.append(float(expSpeckPerc[i]/(100*4)))#for probability density

binst = [2,6,10,14,18,22,26,30,34,38,42,46,50,54,58,62,66,70,74,78,82,86,90,94,98,102,106,110,114,118,122,126,130,134,138,142]


print("time sum 1",sum(norm_time))
print("time sum 2",sum(norm_time2))
print("time sum exp",sum(expSpeckFrac))

fig = plt.figure(1, figsize =(11, 9))  
fig.set_size_inches(10, 8) 
plt.subplots_adjust(left=0.18, bottom=0.15, right=0.9, top=None, wspace=None, hspace=None)
plt.rc('xtick', labelsize = 24)
plt.rc('ytick', labelsize = 24)
plt.subplots_adjust(left=None, bottom=0.15, right=None, top=None, wspace=None, hspace=None)
pl1 = plt.subplot(1,1,1)
plt.plot(expTimeBinShift, expSpeckFrac, '-vg', markersize = 20, linewidth = 4) # histogram of lifetimes
plt.plot(binst[0::], norm_time[0::], '-ok', markersize = 10, linewidth = 4) # histogram of lifetimes
plt.plot(binst[0::], norm_time2[0::], '-sr', markersize = 10, linewidth = 4) # histogram of lifetimes
pl1.set_title('Speckle Lifetime', fontsize = 40)
pl1.set_xlabel('Lifetime (s)', fontsize = 30)
pl1.set_ylabel('Probability denisty', fontsize = 30)
plt.xlim(0,144)
plt.axis([0,144,0,0.1])
plt.xticks([0,10,20,30,40,50,60,70,80,90,100,110,120,130,140], ['0','','20','','40','','60','','80','','100','','120','','140'])
plt.tick_params(length = 8, width = 3) 
plt.legend(labels, fontsize = 24)
plt.show()


counts_reApp = np.zeros(nbins,dtype = int)

#Sort the values for reannealing from appearacne, then bin them like a histogram
reApp.sort()
for i in range(0,len(reApp), 1):
    for j in range(0,nbins,1):
        minj = j*step
        maxj = (j+1)*step
        if minj <= reApp[i] and maxj > reApp[i]:
            counts_reApp[j] = counts_reApp[j] + 1
            break

#eliminate reannealing for 2nd data set
counts_reApp2 = np.zeros(nbins,dtype = int)
#Sort the values for reannealing from appearacne, then bin them like a histogram
reApp2.sort()
for i in range(0,len(reApp2), 1):
    for j in range(0,nbins,1):
        minj = j*step
        maxj = (j+1)*step
        if minj <= reApp2[i] and maxj > reApp2[i]:
            counts_reApp2[j] = counts_reApp2[j] + 1
            break

#Binned appearance profile
x = np.linspace(0,10,20)
a_x = []
expBins = []
for i in range(0,len(x),1):
    if i>0:
        dx = x[i]-x[i-1]
        mid_x = x[i-1] + dx/2
        f_x = dx*(0.85*np.exp(-mid_x/0.1) + 0.15*np.exp(-mid_x/1)) #area = dx (bin size) * area at midpoint (box height)
        a_x.append(float(f_x))
        expBins.append(float(x[i-1]))


apptot = 0
expAppFrac = []
expbinsize = expAppBinL[1]-expAppBinL[0]
for i in range(0,len(expAppPercL),1):
    apptot = expAppPercL[i] + apptot
for i in range(0,len(expAppPercL),1):
#    expAppFrac.append(float(expAppPercL[i]/(apptot)))   
    expAppFrac.append(float(expAppPercL[i]/(0.5*apptot)))   #calculate probability density
    


#labels2 = ['L_crit = 40 mono','L_crit = 60 mono','L_crit = 60 mono','L_crit = 100 mono','Yamashiro et al.']
#labels2 = ['No annealing appearance','Annealing appearance','Yamashiro et al. appearance','No annealing disappearance','Annealing disappearance','Yamashiro et al. disappearance']

bins = bins + 0.25

bins = [0.25,0.75,1.25,1.75,2.25,2.75,3.25,3.75,4.25,4.75,5.25,5.75,6.25]

#experimental appearance and disappearance are already divided by bin size since the bins are 1 um
fig = plt.figure(2, figsize =(11, 9))  
fig.set_size_inches(10, 8) 
plt.subplots_adjust(left=0.18, bottom=0.15, right=0.9, top=None, wspace=None, hspace=None)
plt.rc('xtick', labelsize = 24)
plt.rc('ytick', labelsize = 24)
pl14 = plt.subplot(1,1,1)
plt.plot(expAppBinL, expAppFrac, '-vg', markersize = 15, linewidth = 4) #Yamashiro et al appearance
plt.plot(bins[0:12], norm_zi[0:12], '-ok', markersize = 15, linewidth = 4) #appearance
plt.plot(bins[0:12], norm_zi2[0:12], '-sr', markersize = 15, linewidth = 4) #appearance
plt.axis([0,6,0,1])
plt.legend(labels, fontsize = 18)
pl14.set_xlabel('Distance from leading edge (\u03BCm)', fontsize = 30)
pl14.set_ylabel('Probability density', fontsize = 30)
pl14.set_title('Appearance Location', fontsize = 40) 
plt.xticks([0,1,2,3,4,5,6], ['0','1','2','3','4','5','6']) 
plt.tick_params(length = 8, width = 3) 

plt.show()     

label2 = ['Without enhanced end severing', 'With enhanced end severing']

#experimental appearance and disappearance are already divided by bin size since the bins are 1 um
fig = plt.figure(3, figsize =(11, 9))  
fig.set_size_inches(10, 8) 
plt.subplots_adjust(left=0.18, bottom=0.15, right=0.9, top=None, wspace=None, hspace=None)
plt.rc('xtick', labelsize = 24)
plt.rc('ytick', labelsize = 24)
plt.subplots_adjust(left=None, bottom=0.15, right=None, top=None, wspace=None, hspace=None)
pl14 = plt.subplot(1,1,1)
plt.plot(bins[0:12], norm_zf[0:12], '-ok', markersize = 15, linewidth = 4) #disappearance
plt.plot(bins[0:12], norm_zf2[0:12], '-sr', markersize = 15, linewidth = 4) #disappearance
plt.axis([0,6,0,0.5])
plt.legend(label2, fontsize = 18)
pl14.set_xlabel('Distance from leading edge (\u03BCm)', fontsize = 30)
pl14.set_ylabel('Probability density', fontsize = 30)
pl14.set_title('Disappearance Location', fontsize = 40) 
plt.xticks([0,1,2,3,4,5,6], ['0','1','2','3','4','5','6']) 
plt.tick_params(length = 8, width = 3) 

plt.show()     
