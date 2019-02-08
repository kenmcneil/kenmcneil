package com.ferguson.cs.product.task.stylyze;

import com.ferguson.cs.product.task.stylyze.model.StylyzeInputProduct;
import com.ferguson.cs.product.task.stylyze.model.StylyzeProduct;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.*;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.batch.item.json.JacksonJsonObjectMarshaller;
import org.springframework.batch.item.json.JsonFileItemWriter;
import org.springframework.batch.item.json.builder.JsonFileItemWriterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.util.List;

@Configuration
public class StylyzeJobConfiguration {

    private SqlSessionFactory sqlSessionFactory;

    @Autowired
    @Qualifier("reporterSqlSessionFactory")
    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
    }

    @Autowired
    private JobBuilderFactory stylyzeJobs;

    @Autowired
    private StepBuilderFactory stylyzeSteps;

    @Bean
    public ItemReader<StylyzeInputProduct> stylyzeProductReader()
    {

        ItemReader<StylyzeInputProduct> reader = new ItemReader<StylyzeInputProduct>() {
            @Autowired
            private StylyzeSettings stylyzeSettings;

            private Integer index = 0;

            @Override
            public StylyzeInputProduct read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
                StylyzeInputProduct product = null;
                List<StylyzeInputProduct> inputData = this.stylyzeSettings.getInputData();
                if (this.index < inputData.size()) {
                    product = inputData.get(index);
                    index++;
                }
                return product;
            }
        };
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
    public JsonFileItemWriter<StylyzeProduct> stylyzeProductWriter(){
        return new JsonFileItemWriterBuilder<StylyzeProduct>()
                .jsonObjectMarshaller(new JacksonJsonObjectMarshaller<>())
                .resource(new ClassPathResource("stylyze-output.json"))
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