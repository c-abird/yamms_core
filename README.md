# YammsCore
YammsCore is a pure Java library for micromagnetic simulation using the method 
of finite differences.
It uses the method introduced by Newell to calculate the strayfield using the FFT.
The FFT is calculated using the Java library JTransforms by Piotr Wendykier.

## Storage
The magnetization snapshots are stored using the ASCII omf format as introduced by the
OOMMF simulator.
Scalar values are stored using a simple CSV format.


## Solving the muMag standard problem 4:
The solution of the standard problem 4 as defined by the myMag group could like like the following:

    // Read the initial magnetization from an OMF file
    RealVectorField m = OmfFileService.readFile("sp4-start.omf");
    
    // Define the spatially constant saturation magnetization
    RealScalarField ms = new RealConstantScalarField(m.topology, 8e5);
    
    // Set up the different field constituents
    CompositeFieldTerm field = new CompositeFieldTerm();
    
    field.addFieldTerm(new ExchangeField(13e-12, ms));
    field.addFieldTerm(new DemagField(m.topology));
    field.addFieldTerm(new StaticZeemanField(new double[] { -24.6e-3 / Constants.MU0, 4.3e-3 / Constants.MU0, 0 }));
    
    // Initialize the model (the specific ODE, in this case the regular LLG)
    Model model = new BasicModel(2.211e5, 0.02, ms, field);
    
    // Initialize solver (time integrator)
    Solver solver = new HeunSolver(5e-14, 100, 0);
    
    // Register OmfStorageHandler to save the magnetization every 1000ths step
    solver.addHandler(new OmfStorageHandler("omf", "data-"), 1000);

    // Register ScalarStorageHandler to save every 100ths step
    solver.addHandler(new ScalarStorageHandler("result.table"), 100);
    
    // Start the simulation
    solver.integrate(model, m);

Copyright (C) 2009-2010 Claas Abert, Gunnar Selke

Distributed under the LGPL. See LICENSE file.
