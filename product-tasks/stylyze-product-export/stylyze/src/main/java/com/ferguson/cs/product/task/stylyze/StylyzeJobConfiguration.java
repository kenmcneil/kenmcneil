package com.ferguson.cs.product.task.stylyze;

import com.ferguson.cs.product.task.stylyze.model.StylyzeInputProduct;
import com.ferguson.cs.product.task.stylyze.model.StylyzeProduct;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.json.JacksonJsonObjectMarshaller;
import org.springframework.batch.item.json.JsonFileItemWriter;
import org.springframework.batch.item.json.builder.JsonFileItemWriterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

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
    public FlatFileItemReader<StylyzeInputProduct> stylyzeProductReader()
    {
        FlatFileItemReader<StylyzeInputProduct> reader = new FlatFileItemReader<>();
        reader.setResource(new ClassPathResource("stylyze-input.csv"));
        reader.setLinesToSkip(1);
        reader.setLineMapper(new DefaultLineMapper() {
            {
                setLineTokenizer(new DelimitedLineTokenizer() {
                    {
                        setNames(new String[] {"familyId", "categoryId"});
                    }
                });
                setFieldSetMapper(new BeanWrapperFieldSetMapper<StylyzeInputProduct>() {
                    {
                        setTargetType(StylyzeInputProduct.class);
                    }
                });
            }
        });
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
    public Step stylyzeStep(FlatFileItemReader<StylyzeInputProduct> stylyzeProductReader, ItemProcessor<StylyzeInputProduct, StylyzeProduct> processor, JsonFileItemWriter<StylyzeProduct> writer) {
        return stylyzeSteps.get("stylyzeStep").<StylyzeInputProduct, StylyzeProduct> chunk(100).reader(stylyzeProductReader).processor(processor).writer(writer).build();
    }

    @Bean(name = "stylyze")
    public Job stylyzeJob(@Qualifier("stylyzeStep") Step stylyzeStep) {
        return stylyzeJobs.get("stylyze").start(stylyzeStep).build();
    }

}