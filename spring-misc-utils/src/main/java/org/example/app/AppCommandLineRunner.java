package org.example.app;

import javax.inject.Inject;
import javax.inject.Named;

import org.example.work.WorkWagesFileRenamer;
import org.springframework.boot.CommandLineRunner;

@Named
public class AppCommandLineRunner implements CommandLineRunner {
	@Inject
	private WorkWagesFileRenamer workWagesFileRenamer;

	@Override
	public void run(String... args) throws Exception {
		//workWagesFileRenamer.run(inputPath);
	}

}
