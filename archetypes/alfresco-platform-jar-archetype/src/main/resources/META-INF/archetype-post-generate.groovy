def file = new File( request.getOutputDirectory(), request.getArtifactId()+"/run.sh" );
file.setExecutable(true, false);