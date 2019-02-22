package com.ferguson.cs.product.task.stylyze;

import com.ferguson.cs.product.task.stylyze.model.StylyzeInputProduct;
import com.ferguson.cs.product.task.stylyze.model.StylyzeProduct;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.*;
import org.springframework.batch.item.json.JacksonJsonObjectMarshaller;
import org.springframework.batch.item.json.JsonFileItemWriter;
import org.springframework.batch.item.json.builder.JsonFileItemWriterBuilder;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;

@Configuration
public class StylyzeJobConfiguration {

    private SqlSessionFactory reporterSqlSessionFactory;

    @Autowired
    @Qualifier("reporterSqlSessionFactory")
    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        this.reporterSqlSessionFactory = sqlSessionFactory;
    }

    @Autowired
    private StylyzeSettings stylyzeSettings;

    @Autowired
    private JobBuilderFactory stylyzeJobs;

    @Autowired
    private StepBuilderFactory stylyzeSteps;

    @Value("${spring.application.name}")
    private String applicationName;

    public Resource getTempResource(String prefix, String fileType) throws IOException {
        String tmpDirStr = System.getProperty("java.io.tmpdir");
        File tempFolder =  new File(tmpDirStr.concat("/").concat(applicationName));
        tempFolder.mkdirs();
        File tempFile = File.createTempFile(prefix + "-", "." + fileType, tempFolder);
        return new FileSystemResource(tempFile);
    }

    @Bean
    public ItemReader<StylyzeInputProduct> stylyzeProductReader()
    {
        ItemReader<StylyzeInputProduct> reader = new ListItemReader<>(this.stylyzeSettings.getInputData());
        return reader;
    }

    @Bean
    public ItemProcessor<StylyzeInputProduct, StylyzeProduct> stylyzeProcessor(){
        return new StylyzeItemProcessor();
    }

    /**
     * This function defines the writer that will be used to output the converted file.
     * A chunk of data as defined by the job is written at a time.
     * @return writer
     */
    @Bean
    public JsonFileItemWriter<StylyzeProduct> stylyzeProductWriter() throws IOException {
        return new JsonFileItemWriterBuilder<StylyzeProduct>()
                .jsonObjectMarshaller(new JacksonJsonObjectMarshaller<>())
                .resource(this.getTempResource("stylyze-output", "json"))
                .name("stylyzeProductWriter")
                .build();
    }

    @Bean
    public Step stylyzeStep(
            ItemReader<StylyzeInputProduct> stylyzeProductReader,
            ItemProcessor<StylyzeInputProduct, StylyzeProduct> processor,
            JsonFileItemWriter<StylyzeProduct> writer) {
        return stylyzeSteps
                .get("stylyzeStep")
                .<StylyzeInputProduct, StylyzeProduct> chunk(100)
                .reader(stylyzeProductReader)
                .processor(processor)
                .writer(writer)
                .faultTolerant()
                .skipLimit(10)
                .skip(FlatFileParseException.class)
                .build();
    }

    @Bean(name = "stylyze")
    public Job stylyzeJob(@Qualifier("stylyzeStep") Step stylyzeStep) {
        return stylyzeJobs
                .get("stylyze")
                .start(stylyzeStep)
                .build();
    }

}