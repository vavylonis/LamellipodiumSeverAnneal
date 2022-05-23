# LamellipodiumSeverAnneal

This code simulates a dendritic actin network undergoing retrograde flow, including processes of filament severing into oligomers and annealing. This code was developed for the simulations in D. Holz, A. R. Hall, E. Usukura, S. Yamashiro, N. Watanabe and D. Vavylonis (bioRxiv https://doi.org/10.1101/2021.03.31.437985)

Compilation using java: Run SRC/Filament_3D_App.java. This calls Filament_3D.java, NetworkList.java, NetworkMember.java, Speckles.java, importParam.java, MHelp.java and DataWriter.java. 

Input parameter file (filParams.txt): This file lists parameters used by the simulation. File is read in by SRC/importParam.java. The parameters folder includes XTC and keratocyte annealing parameter files with and without enhanced end severing.  For simulations without annealing, k_oligomerAnneal should be set to a small non-zero number (i.e. 1e-19) and the time oligomers remain in the simulation should be decreased (i.e. modify 20.00 to 0.01 s) directly in the code ("else if((oligomer.fil_age- oligomer.oligomer_time) > 20.00 )" on line 279 in SRC/Filament_3DApp). The first modification is to ensure oligomers are created in the simulation.  A value of k_oligomerAnneal = 0 will classify oligomers as short filaments.  The second modification is to ensure oligomers do not accumulate and slow down the simulation.  

Visualization: Ovito/LamToOvitoBranchMovieEnds2.py reads in saved filament states saved from SRC/Filament_3D_app.java and converts to a file structure Ovito can read.  

Plotting scripts: This folder includes python scripts used for plotting data from the data folder. Subfolders indicate the cell type of the parameter set used with these scripts. To plot cases without annealing or the cases found in the supplemental material, change the folder name where the file is imported. Following summarize the included scripts: 
BEPlot.py: plots the barbed end distribution
BranchPlot.py: plots the branch distribution 
ConcentrationPlot.py: plots the F-actin concentration profile
LengthPlot.py: plots the cumulative length distribution of filaments 
SpeckCompareExperiment.py: Plots speckle lifetime, appearance and disappearance profiles
ShortLifetimes.py: plots the short speckle lifetimes for XTC parameters (Figure 6B)
BEDistribution_RapidBEDepoly.py: plots the barbed end distributions used for rapid barbed end depolymerization followed by rapid polymerization (Figure 4 - Supplement 4E)
  
Data:  This folder includes the data used for plotting.  Subfolders are labeled to indicate the parameter set used.  The beggining of the folder name indicates the cell parameter set, followed by inclusion or exclusion of enhanced end severing, and followed by the presense or absence of annealing.  Cases used in the supplemental material are given an additional label at the end. The label "2x_kbr" indicates the data resulted from simulations with double the branching rate (Figure 5 - Supplement 3), "EndExclusion" indicates the data was for comparison between end severing of polymerizing ends (Figure 5 - Supplement 4) and the folder "Keratocyte_BEDestabilization" was used for the alternate model of rapid barbed end depolymerization followed by rapid polymerization (Figure 4 - Supplement 4). 
