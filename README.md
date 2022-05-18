# LamellipodiumSeverAnneal

This code simulates a dendritic actin network undergoing retrograde flow, including processes of filament severing into oligomers and annealing. This code was developed for the simulations in D. Holz, A. R. Hall, E. Usukura, S. Yamashiro, N. Watanabe and D. Vavylonis (bioRxiv https://doi.org/10.1101/2021.03.31.437985)

Compilation using java: Run SRC/Filament_3D_App.java. This calls Filament_3D.java, NetworkList.java, NetworkMember.java, Speckles.java, importParam.java, MHelp.java and DataWriter.java. 

Input parameter file (filParams.txt): This file lists parameters used by the simulation. File is read in by SRC/importParam.java. The parameters folder includes XTC and keratocyte annealing parameter files with and without enhanced end severing.  For simulations without annealing, k_oligomerAnneal should be set to a small non-zero number (i.e. 1e-19) and the time oligomers are remain in the simulation should be decreased ("else if((oligomer.fil_age- oligomer.oligomer_time) > 20.00 )" on line 279 in SRC/Filament_3DApp). These modifications include oligomers in the simulation and increased runtime since oligomers are static without annealing.  

Visualization: Ovito/LamToOvitoBranchMovieEnds2.py reads in saved filament states saved from SRC/Filament_3D_app.java and converts to a file structure Ovito can read.  
