# read in the .csv for lamellipodium state and convert to a file Ovito understands 
#place lamellipodium state files in a folder named "Structure".  Files will be read from here.
#Also and a folder named "movie" inside this folder to print the files for Ovito to. 
#when reading in files, it reads every file in the directory so you will get an warning when it 
#tries to read in "movie" but it is not an issue for the output files

import math
from pathlib import Path
​
def periodicWrap(dist, boxl):
    return dist - boxl*round(dist / boxl)
​
# 1 um distance in x
boxlx = 1
boxly = 0.2
Time = 350
TimeInt = 1
​
path1 = 'D:\dah414\\FilamentousLamellipodia\\Ovito\\Structure'
​
paths = Path(path1).glob('*')
for path in paths:
    
    print(path)
    
    # because path is object not string
   # path_in_str = str(path)
    # Do thing with the path
    #print(path_in_str)
    filepath = str(path)
​
#filepath = 'Sev_5E-2-Ann_60pt22M-Psp_pt01'
#filepath = 'LessDense2'
#filepath = 'LamState'
​
#for filepath in path_in_str:
    
    Time = Time + TimeInt
    
    outputBeads = []
    filamentStartStopIndeces = []
​
    filamentIndex = 0
​
    skipHeader = True  
​
    output_str = ''
    output_bnd_str = ''
​
    num_beads = 0
    num_bonds = 0
    num_branch = 0
    num_BE = 0
    num_PE = 0
​
    with open(filepath) as fp:
        line = fp.readline()
        
        while line:
            splitString = line.split(',')
            
            # if line is a filament split the string accordingly and get pointed end location, direction and length
            if(len(splitString) >= 15):
                if skipHeader == True:
                    skipHeader = False
                else:
                    if int(splitString[4]) > 1: # don't add filaments that are of length 0 or 1
                    
                        currFilamentTag = int(splitString[1])
                        
                        motherFilamentTag = int(splitString[2])
                        motherFilamentBead = int(splitString[3])
                        
                        lengthOfFilament = int(splitString[4]) * 2.7 / 1000.0
                        
                      #  if lengthOfFilament < ((2*2.7)/1000): # don't add filaments that are of length 0 or 1
                      #      continue
                        
                        pointedEndX = float(splitString[5])-(boxlx/2) #shift points so zero lies in the center of the box
                        pointedEndY = float(splitString[6])#-0.1
                        pointedEndZ = float(splitString[7])
                        
                   #     pointedEndX = periodicWrap(pointedEndX, boxlx)
                   #     pointedEndY = periodicWrap(pointedEndY, boxly)
                        
                        
                        directionX = float(splitString[8])
                        directionY = float(splitString[9])
                        directionZ = float(splitString[10])
                        
                        capped = int(float(splitString[11]))
                        
                        ''' # this is for debugging
                        #normalize direction even though it should already be normalized 
                      #  norm2 = directionX*directionX + directionY*directionY + directionZ*directionZ
                      #  norm = math.sqrt(norm2)
                        '''
                        '''
                        pointedEndX = -0.20#float(splitString[5])#-0.5 #shift points so zero lies in the center of the box
                        pointedEndY = 0#float(splitString[6])#-0.1
                        pointedEndZ = 2#float(splitString[7])
                                        
                        directionX = 1/math.sqrt(2)#float(splitString[8])
                        directionY = 0#float(splitString[9])
                        directionZ = -1/math.sqrt(2)#float(splitString[10])
                        
                       # norm2 = directionX*directionX + directionY*directionY + directionZ*directionZ
                      #  norm = math.sqrt(norm2)
                        
                      #  directionX = directionX/norm
                      #  directionY = directionY/norm
                      #  directionZ = directionZ/norm
                        
                        lengthOfFilament = 2.5#1000 * 0.0027
                        '''
                        barbedEndX = pointedEndX + directionX * lengthOfFilament ##############################
                        barbedEndY = pointedEndY + directionY * lengthOfFilament ##############################
                        barbedEndZ = pointedEndZ + directionZ * lengthOfFilament ##############################
​
                        pointedEndX = periodicWrap(pointedEndX, boxlx)
                    #    pointedEndY = periodicWrap(pointedEndY, boxly)
​
                        barbedEndX = periodicWrap(barbedEndX, boxlx) 
                     #   barbedEndY = periodicWrap(barbedEndY, boxly)
                        
                        output_str += str(num_beads+1) + ' ' + str(currFilamentTag) + ' ' +  str(1) + ' ' + str(pointedEndX) + ' ' + str(pointedEndY) + ' ' + str(pointedEndZ) + '\n'
                        num_beads += 1
                        
                        output_str += str(num_PE+1) + ' ' + str(currFilamentTag) + ' ' +  str(4) + ' ' + str(pointedEndX) + ' ' + str(pointedEndY) + ' ' + str(pointedEndZ) + '\n'
                        num_PE += 1
                        
                        branchX = pointedEndX
                        branchY = pointedEndY
                        branchZ = pointedEndZ
                        
                        bound = 0
                        if directionX > 0 and pointedEndX > 0:
                            bound = boxlx/2
                        elif directionX < 0 and pointedEndX < 0:
                            bound = -boxlx/2
                        else:
                            bound = 0
​
                        distToBoundX = abs(bound-pointedEndX)
                        
                        while lengthOfFilament*abs(directionX) > distToBoundX:
                            fracLen = distToBoundX/(lengthOfFilament*abs(directionX)) #this is the fraction of filament that should be drawn in the x direction
                            lenToBound = fracLen*lengthOfFilament   #same fraction should be drawn in all directions
                            currBEx = bound#Sign*(boxlx/2)   #current point is at the boundary
                            currBEy = pointedEndY + directionY*lenToBound
                            currBEz = pointedEndZ + directionZ*lenToBound
​
                            output_str += str(num_beads+1) + ' ' + str(currFilamentTag) + ' ' +  str(1) + ' ' + str(currBEx) + ' ' + str(currBEy) + ' ' + str(currBEz) + '\n'
                            output_bnd_str += str(num_bonds+1) + ' 1 ' + str(num_beads) + ' ' + str(num_beads+1) + '\n'
                            num_beads += 1
                            num_bonds += 1
​
                            lengthOfFilament = lengthOfFilament - lenToBound - 0.0027 #*directionX
​
                            #new PE for next section of the filament
                            pointedEndX = currBEx + directionX*0.0027#-boundSign*(boxlx/2)#-0.0027)#-sign*(boxlx/2-0.001)
                            pointedEndY = currBEy + directionY*0.0027
                            pointedEndZ = currBEz + directionZ*0.0027
​
                            pointedEndX = periodicWrap(pointedEndX, boxlx)
                       #     pointedEndY = periodicWrap(pointedEndY, boxly)
​
                           #distToBoundX = abs(boundSign*(boxlx/2) - pointedEndX)
​
                            if directionX > 0 and pointedEndX > 0:
                                bound = boxlx/2
                            elif directionX < 0 and pointedEndX < 0:
                                bound = -boxlx/2
                            elif directionX > 0 and pointedEndX < 0:
                                bound = 0
                            elif directionX < 0 and pointedEndX > 0:
                                bound = 0
                        
                            distToBoundX = abs(bound-pointedEndX)
​
                            #new BE for next portion
                            barbedEndX = pointedEndX + directionX * lengthOfFilament ##############################
                            barbedEndY = pointedEndY + directionY * lengthOfFilament ##############################
                            barbedEndZ = pointedEndZ + directionZ * lengthOfFilament ##############################
                                
                            barbedEndX = periodicWrap(barbedEndX, boxlx) 
                        #    barbedEndY = periodicWrap(barbedEndY, boxly)
                            
                            output_str += str(num_beads+1) + ' ' + str(currFilamentTag) + ' ' +  str(1) + ' ' + str(pointedEndX) + ' ' + str(pointedEndY) + ' ' + str(pointedEndZ) + '\n'
                            num_beads += 1
​
                        output_str += str(num_beads+1) + ' ' + str(currFilamentTag) + ' ' +  str(1) + ' ' + str(barbedEndX) + ' ' + str(barbedEndY) + ' ' + str(barbedEndZ) + '\n'
                        output_bnd_str += str(num_bonds+1) + ' 1 ' + str(num_beads) + ' ' + str(num_beads+1) + '\n'
                        
                        num_beads += 1
                        num_bonds += 1
                        
                        if capped == 1:
                            output_str += str(num_BE+1) + ' ' + str(currFilamentTag) + ' ' +  str(5) + ' ' + str(barbedEndX) + ' ' + str(barbedEndY) + ' ' + str(barbedEndZ) + '\n'
                            num_BE += 1
                        elif capped == 0 or capped == 2:
                            output_str += str(num_BE+1) + ' ' + str(currFilamentTag) + ' ' +  str(6) + ' ' + str(barbedEndX) + ' ' + str(barbedEndY) + ' ' + str(barbedEndZ) + '\n'
                            num_BE += 1
                        
                        #add beads in branch locations
                        if motherFilamentTag != 0:
                            output_str += str(num_branch+1) + ' ' + str(currFilamentTag) + ' ' +  str(2) + ' ' + str(branchX) + ' ' + str(branchY) + ' ' + str(branchZ) + '\n'
                            num_branch += 1
                          
                    filamentIndex += 1
                
                    
            line = fp.readline()
​
    #place beads to know the location to take snapshots
    output_str += str(1) + ' ' + str(123456789) + ' ' +  str(3) + ' ' + str(0) + ' ' + str(0) + ' ' + str(1) + '\n'
    output_str += str(1) + ' ' + str(123456788) + ' ' +  str(3) + ' ' + str(0) + ' ' + str(0) + ' ' + str(2) + '\n'
    output_str += str(1) + ' ' + str(123456787) + ' ' +  str(3) + ' ' + str(0) + ' ' + str(0) + ' ' + str(3) + '\n'
    output_str += str(1) + ' ' + str(123456786) + ' ' +  str(3) + ' ' + str(0) + ' ' + str(0) + ' ' + str(4) + '\n'
​
    total_beads = num_beads+num_branch+4+num_BE+num_PE
​
    output_lammps = open(path1 + '\\movie\\LamTime_' + str(Time) + '.lmps', 'w')
    output_lammps.write('LAMMPS file\n\n')
    output_lammps.write(str(total_beads) + ' atoms\n')
    output_lammps.write(str(num_bonds) + ' bonds\n\n')
    output_lammps.write(str(6) + ' atom types\n')
    output_lammps.write(str(1) + ' bond types\n\n')
    output_lammps.write('-1.001 1.001 xlo xhi\n')
    #output_lammps.write('-0.1 0.1 ylo yhi\n')
    #output_lammps.write('-0.501 0.501 xlo xhi\n')
    output_lammps.write('-100.01 100.201 ylo yhi\n')
    output_lammps.write('-10000.0 10000.0 zlo zhi\n\n')
    output_lammps.write('Atoms\n\n')
​
    output_lammps.write(output_str)
​
    output_lammps.write('\nBonds\n\n')
        
    output_lammps.write(output_bnd_str)
​
    output_lammps.close()
