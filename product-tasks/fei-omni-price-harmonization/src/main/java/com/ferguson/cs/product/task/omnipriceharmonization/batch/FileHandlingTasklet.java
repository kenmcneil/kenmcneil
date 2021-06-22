package com.ferguson.cs.product.task.omnipriceharmonization.batch;

import java.io.File;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import com.ferguson.cs.product.task.omnipriceharmonization.OmniPriceHarmonizationSettings;

public class FileHandlingTasklet implements Tasklet {

	private final OmniPriceHarmonizationSettings omniPriceHarmonizationSettings;

	public FileHandlingTasklet(OmniPriceHarmonizationSettings omniPriceHarmonizationSettings) {
		this.omniPriceHarmonizationSettings = omniPriceHarmonizationSettings;
	}

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

		IOFileFilter ioFileFilter = FileFilterUtils.and(FileFilterUtils.prefixFileFilter("Daily_Harmonization_Report",IOCase.INSENSITIVE),FileFilterUtils.suffixFileFilter(".csv", IOCase.INSENSITIVE));
		Collection<File> sourceFiles = FileUtils.listFiles(new File(omniPriceHarmonizationSettings.getIncomingFilePath()),ioFileFilter,null);
		File archiveDirectory = new File(omniPriceHarmonizationSettings.getArchivePath());
		for(File file : sourceFiles) {
			FileUtils.moveFileToDirectory(file,archiveDirectory,false);
		}

		return RepeatStatus.FINISHED;
	}
}
