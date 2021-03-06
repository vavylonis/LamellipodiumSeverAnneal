# Read in speckle csv file to plot lifetime 

import csv
import matplotlib.pyplot as plt
import numpy as np

NoRA = False  # true is exclusion of reannealed speckles

ti = float(230.0)
tf = float(250.0)

binSt = 0.1  #bin sizes for probability density
binS = 0.5

max_z = 1 # don't include speckles that are further from the leading edge than this value


nb = 11 # number of bins for normalization, graphing
nbt = 1440

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

time_f3 = []
time_i3 = []
z_i3 = []
z_f3 = []
delta_t3 = []
delta_z3 = []
delta_x3 = []
delta_y3 = []
annDist3 = []
reanneal3 = []
type3 = []
reApp3 = []

nbins = 24 # linspace starts at zero and ends at the max value 
max = 12
step = max/(nbins)


#nbinst = 36
#maxt = 144
nbinst = 1440
maxt = 144
stept = maxt/(nbinst)

sum_time = 0
sum_zi = 0 
sum_zf = 0 

sum_time2 = 0
sum_zi2 = 0 
sum_zf2 = 0 

sum_time3 = 0
sum_zi3 = 0 
sum_zf3 = 0 

expTimeBin = [0.15,0.25,0.35,0.45,0.55,0.65,0.75,0.85,0.95,1.05,1.15,1.25,1.35,1.45,1.55,1.65,1.75,1.85,1.95,2.05,2.15,2.25,2.35,2.45,2.55,2.65,2.75,2.85,2.95]
expSpeckNum = [5,6,7,2,5,1,4,4,2,1,2,1,3,1,2,0,4,1,0,3,2,1,1,1,5,3,3,1,53.7]

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

if NoRA:
    with open('XTC_WithEndSev_NoAnn\\reannealAppearance.csv', 'r') as file: 
        raFile = csv.reader(file)
        for row in raFile: 
            reApp.append(float(row[0]))
            
#read in file for no annealing/leading edge model
with open('EndS_1E-3_UnifS_5E-6_Ann_60_Lcrit_150\\EndSevAnn_NoSevExclusionLE\\EndS_1E-3_UnifS_5E-6_Ann_60_Lcrit_150\\Speckles.csv', 'r') as file:
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
                
if NoRA:
    with open('EndS_1E-3_UnifS_5E-6_Ann_60_Lcrit_150\\reannealAppearance.csv', 'r') as file: 
        raFile = csv.reader(file)
        for row in raFile: 
            reApp2.append(float(row[0]))
            
#read in file for no annealing/leading edge model
with open('EndS_1E-3_UnifS_5E-6_Ann_60_Lcrit_150\\EndSevAnn_NoSevExclusionLE\\EndS_2E-3_UnifS_0_Ann_60_Lcrit_150\\Speckles.csv', 'r') as file:
    speckFile = csv.reader(file)

 #   header = speckFile.next(speckFile) #skip first line/header of CSV file
    next(speckFile)

    for row in speckFile:
        if row[0] == 'Time':
            continue
        elif ti <= float(row[0]) and tf > float(row[0]): #only look at a portion of all the events  
            time_f3.append(float(row[0])) #time -- db
            #speckle_id -- int
            #dist_from_PE -- int
            time_i3.append(float(row[3])) #init_spec_time -- db
            #Fil_ID -- int
            #init_x -- db
            #init_y -- db
            z_i3.append(float(row[7])) #init_z -- db
            #fin_x -- db
            #fin_y -- db
            z_f3.append(float(row[10])) #fin_z -- db
            #type -- int
            t = float(row[0]) - float(row[3]) 
            delta_t3.append(t)
            z = float(row[10]) - float(row[7])
            delta_z3.append(z)
            x = float(row[8]) - float(row[5])
            delta_x3.append(x)
            y = float(row[9]) - float(row[6])
            delta_y3.append(y)
            dist = np.sqrt(x*x + y*y + z*z)
            annDist3.append(dist)
            type3.append(float(row[11]))
            if t <= 2:# and z <= 0.2:  # how far it would move backward with retrograde flow
                reanneal3.append(True) # speckle reannealed
            else: 
                reanneal3.append(False) # speckle did not reanneal
                
if NoRA:
    with open('EndS_1E-3_UnifS_5E-6_Ann_60_Lcrit_150\\reannealAppearance.csv', 'r') as file: 
        raFile = csv.reader(file)
        for row in raFile: 
            reApp3.append(float(row[0]))
              
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

counts_time3 = np.zeros(nbinst,dtype = int)
counts_zi3 = np.zeros(nbins,dtype = int)
counts_zf3 = np.zeros(nbins,dtype = int)
binst3 = np.zeros(nbinst, dtype = float)
bins3 = np.zeros(nbins, dtype = float)

n0_1s = 0
n1_2s = 0
n2_3s = 0
for i in range(0,len(delta_t),1):
    if z_i[i] <= max_z: #and z_f[i] <= max_z: #exclude anything that's outside max_z um 
        #if NoRA and reanneal[i]: # eliminate reannlealing 
        #    continue     
        if 0 <= delta_t[i] and 1 > delta_t[i]:
            n0_1s = n0_1s + 1
        elif 1 <= delta_t[i] and 2 > delta_t[i]:
            n1_2s = n1_2s + 1  
        elif 2 <= delta_t[i] and 3 > delta_t[i]:
            n2_3s = n2_3s + 1            
        for j in range(0,nbinst,1): 
            minj = round(j*stept,4)
            maxj = round((j+1)*stept,4)
            curr_t = round(delta_t[i],4)
            if minj <= curr_t and maxj > curr_t:
                counts_time[j] = counts_time[j] + 1


t_tot = sum(counts_time[0:nbt])
norm_time = counts_time/(t_tot*binSt)
frac_counts = counts_time/(sum(counts_time))


#for set 2 without annealing
n0_1s2 = 0
n1_2s2 = 0
n2_3s2 = 0
for i in range(0,len(delta_t2),1):
    if z_i2[i] <= max_z: #and z_f2[i] <= max_z:
        for j in range(0,nbinst,1): 
            minj = round(j*stept,4)
            maxj = round((j+1)*stept,4)
            curr_t = round(delta_t2[i],4)
            if minj <= curr_t and maxj > curr_t:
                counts_time2[j] = counts_time2[j] + 1



t_tot2 = sum(counts_time2[0:nbt])
norm_time2 = counts_time2/(t_tot2*binSt)
frac_counts2 = counts_time2/(sum(counts_time2))


#for set 3
n0_1s3 = 0
n1_2s3 = 0
n2_3s3 = 0
for i in range(0,len(delta_t3),1):
    if z_i3[i] <= max_z: #and z_f2[i] <= max_z:
        for j in range(0,nbinst,1): 
            minj = round(j*stept,4)
            maxj = round((j+1)*stept,4)
            curr_t = round(delta_t3[i],4)
            if minj <= curr_t and maxj > curr_t:
                counts_time3[j] = counts_time3[j] + 1
                
t_tot3 = sum(counts_time3[0:nbt])
norm_time3 = counts_time3/(t_tot3*binSt)
frac_counts3 = counts_time3/(sum(counts_time3))

#set up values for the bins for the x-axis
for i in range(0,nbinst,1):
    binst[i] = i*stept
          
for i in range(0,nbins,1):
    bins[i] = i*step

labels = ['Experiment','Uniform severing', 'Enhanced end severing']


expTimeBinShift = []
expTimeBinShift.append(float(1.0))
expSpeckFrac = []
for i in range(0,len(expSpeckNum),1):
    expSpeckFrac.append(float(expSpeckNum[i]/(sum(expSpeckNum)*0.1)))#for probability density

binst = [0.05,0.15,0.25,0.35,0.45,0.55,0.65,0.75,0.85,0.95,1.05,1.15,1.25,1.35,1.45,1.55,1.65,1.75,1.85,1.95,2.05,2.15,2.25,2.35,2.45,2.55,2.65,2.75,2.85,2.95]

print("time sum 1",sum(norm_time))
print("time sum 2",sum(norm_time2))
print("time sum exp",sum(expSpeckFrac))

print("fraction within 3s:")
print("for case 1: ", sum(counts_time[0:30])/sum(counts_time), " total in 3s: ", sum(frac_counts[0:29]), " total: ", sum(frac_counts))
print("for case 2: ", sum(counts_time2[0:30])/sum(counts_time2), " total in 3s: ", sum(frac_counts2[0:29]), " total: ", sum(frac_counts2))
print("for case 3: ", sum(counts_time3[0:30])/sum(counts_time3), " total in 3s: ", sum(frac_counts3[0:29]), " total: ", sum(frac_counts3))
print("case 1 total in 0.5 s: ", sum(frac_counts[0:4]), " case 1 total in 1 s: ", sum(frac_counts[0:9]))
print("case 2 total in 0.5 s: ", sum(frac_counts2[0:4]), " case 2 total in 1 s: ", sum(frac_counts2[0:9]))
print("case 3 total in 0.5 s: ", sum(frac_counts3[0:4]), " case 3 total in 1 s: ", sum(frac_counts3[0:9]))
print("for experiment: ", sum(expSpeckNum[0:28])/sum(expSpeckNum))

fig = plt.figure(1, figsize =(11, 9))  
fig.set_size_inches(10, 8) 
plt.subplots_adjust(left=0.18, bottom=0.15, right=0.9, top=None, wspace=None, hspace=None)
plt.rc('xtick', labelsize = 24)
plt.rc('ytick', labelsize = 24)
plt.subplots_adjust(left=None, bottom=0.15, right=None, top=None, wspace=None, hspace=None)
pl1 = plt.subplot(1,1,1)
plt.plot(expTimeBin[0:28], expSpeckFrac[0:28], '-vg', markersize = 20, linewidth = 4) # histogram of lifetimes
plt.plot(binst[0:30], norm_time[0:30], '-ok', markersize = 10, linewidth = 4) # histogram of lifetimes
plt.plot(binst[0::], norm_time2[0:30], '-sr', markersize = 10, linewidth = 4) # histogram of lifetimes
pl1.set_title('Speckle Lifetime', fontsize = 40)
pl1.set_xlabel('Lifetime (s)', fontsize = 30)
pl1.set_ylabel('Probability denisty', fontsize = 30)
plt.xlim(0,3)
plt.axis([0,3,0,0.6])
plt.tick_params(length = 8, width = 3) 
plt.legend(labels, fontsize = 24)
plt.show()

largeLife = sum(frac_counts[30::])
largeLife2 = sum(frac_counts2[30::])
largeLife3 = sum(frac_counts3[30::])

#Plot uniform severing ###############
fig, (ax, ax2) = plt.subplots(2, 1, sharex=True)#, figsize =(11, 9))
fig.set_size_inches(10, 8) 
fig.subplots_adjust(left=0.18, bottom=0.15, right=0.9, top=None, wspace=None, hspace=None)
ax2.bar(binst[0:30], frac_counts[0:30], color = 'black', width = 0.1, edgecolor = 'white')
ax2.bar(3,largeLife, color = 'black', width = 0.1, edgecolor = 'white')
ax.bar(3,largeLife, color = 'black', width = 0.1, edgecolor = 'white')
ax.set_ylim(0.6, 1.0)  # outliers only
ax2.set_ylim(0, 0.1)  # most of the data

d = .015  # how big to make the diagonal lines in axes coordinates
# arguments to pass to plot, just so we don't keep repeating them
kwargs = dict(transform=ax.transAxes, color='k', clip_on=False)
ax.plot((-d, +d), (-d, +d), **kwargs)        # top-left diagonal
ax.plot((1 - d, 1 + d), (-d, +d), **kwargs)  # top-right diagonal

kwargs.update(transform=ax2.transAxes)  # switch to the bottom axes
ax2.plot((-d, +d), (1 - d, 1 + d), **kwargs)  # bottom-left diagonal
ax2.plot((1 - d, 1 + d), (1 - d, 1 + d), **kwargs)  # bottom-right diagonal

# hide the spines between ax and ax2
ax.spines['bottom'].set_visible(False)
ax2.spines['top'].set_visible(False)
ax.xaxis.tick_top()
ax.tick_params(labeltop=False)  # don't put tick labels at the top
ax2.xaxis.tick_bottom()


plt.rc('xtick', labelsize = 24)
plt.rc('ytick', labelsize = 24)
ax.set_title('Uniform Severing', fontsize = 40)
ax2.set_xlabel('Lifetime (s)', fontsize = 30)
ax2.set_ylabel('Fraction of Speckles', fontsize = 30)
plt.xlim(0,3.05)
plt.tick_params(length = 8, width = 3) 
plt.show()


#plot enhanced end severing
fig, (ax, ax2) = plt.subplots(2, 1, sharex=True)#, figsize =(11, 9))
fig.set_size_inches(7.6, 10) 
fig.subplots_adjust(left=0.18, bottom=0.15, right=0.9, top=None, wspace=None, hspace=None)
ax2.bar(binst[0:29], frac_counts2[0:29], color = 'gray', width = 0.1, edgecolor = 'black')
ax2.bar(3,largeLife2, color = 'gray', width = 0.1, edgecolor = 'black')
ax.bar(3,largeLife2, color = 'gray', width = 0.1, edgecolor = 'black')
ax.set_ylim(0.6, 1.0)  # outliers only
ax2.set_ylim(0, 0.05)  # most of the data

d = .015  # how big to make the diagonal lines in axes coordinates
# arguments to pass to plot, just so we don't keep repeating them
kwargs = dict(transform=ax.transAxes, color='k', clip_on=False)
ax.plot((-d, +d), (-d, +d), **kwargs)        # top-left diagonal
ax.plot((1 - d, 1 + d), (-d, +d), **kwargs)  # top-right diagonal

kwargs.update(transform=ax2.transAxes)  # switch to the bottom axes
ax2.plot((-d, +d), (1 - d, 1 + d), **kwargs)  # bottom-left diagonal
ax2.plot((1 - d, 1 + d), (1 - d, 1 + d), **kwargs)  # bottom-right diagonal

# hide the spines between ax and ax2
ax.spines['bottom'].set_visible(False)
ax2.spines['top'].set_visible(False)
ax.xaxis.tick_top()
ax.tick_params(labeltop=False)  # don't put tick labels at the top
ax2.xaxis.tick_bottom()

ax.set_title('Simulation', fontsize = 40)
plt.rc('xtick', labelsize = 14)
plt.rc('ytick', labelsize = 14)
#ax.set_title('Enhanced End Severing', fontsize = 40)
ax2.set_xlabel('Lifetime (s)', fontsize = 24)
ax2.set_ylabel('Fraction of Speckles', fontsize = 24)
plt.xlim(0,3.05)
plt.tick_params(length = 8, width = 3) 
plt.show()



#plot enhanced end severing
fig, (ax, ax2) = plt.subplots(2, 1, sharex=True)#, figsize =(11, 9))
fig.set_size_inches(10, 8) 
fig.subplots_adjust(left=0.18, bottom=0.15, right=0.9, top=None, wspace=None, hspace=None)
ax2.bar(binst[0:29], frac_counts3[0:29], color = 'blue', width = 0.1, edgecolor = 'black')
ax2.bar(3,largeLife3, color = 'blue', width = 0.1, edgecolor = 'black')
ax.bar(3,largeLife3, color = 'blue', width = 0.1, edgecolor = 'black')
ax.set_ylim(0.6, 1.0)  # outliers only
ax2.set_ylim(0, 0.05)  # most of the data

d = .015  # how big to make the diagonal lines in axes coordinates
# arguments to pass to plot, just so we don't keep repeating them
kwargs = dict(transform=ax.transAxes, color='k', clip_on=False)
ax.plot((-d, +d), (-d, +d), **kwargs)        # top-left diagonal
ax.plot((1 - d, 1 + d), (-d, +d), **kwargs)  # top-right diagonal

kwargs.update(transform=ax2.transAxes)  # switch to the bottom axes
ax2.plot((-d, +d), (1 - d, 1 + d), **kwargs)  # bottom-left diagonal
ax2.plot((1 - d, 1 + d), (1 - d, 1 + d), **kwargs)  # bottom-right diagonal

# hide the spines between ax and ax2
ax.spines['bottom'].set_visible(False)
ax2.spines['top'].set_visible(False)
ax.xaxis.tick_top()
ax.tick_params(labeltop=False)  # don't put tick labels at the top
ax2.xaxis.tick_bottom()


plt.rc('xtick', labelsize = 24)
plt.rc('ytick', labelsize = 24)
#ax.set_title('High Enhanced End Severing', fontsize = 40)
ax2.set_xlabel('Lifetime (s)', fontsize = 30)
ax2.set_ylabel('Fraction of Speckles', fontsize = 30)
plt.xlim(0,3.05)
plt.tick_params(length = 8, width = 3) 
plt.show()

