package id.ac.ui.cs.advprog.yomu.learningandquiz.component;

import id.ac.ui.cs.advprog.yomu.learningandquiz.model.Question;
import id.ac.ui.cs.advprog.yomu.learningandquiz.model.ReadingMaterial;
import id.ac.ui.cs.advprog.yomu.learningandquiz.service.ReadingMaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.util.Arrays;

@Component
public class DataSeeder implements CommandLineRunner {

    private final ReadingMaterialService service;

    @Autowired
    public DataSeeder(ReadingMaterialService service) {
        this.service = service;
    }

    @Override
    public void run(String... args) throws Exception {

        ReadingMaterial code = new ReadingMaterial();
        code.setTitle("Basic Java");
        code.setCategory("Code");
        code.setContent("In Java, the main method signature is: public static void main(String[] args).");
        code.setProgress(0);
        code.setTimeLimit(30);

        Question qCode = new Question();
        qCode.setQuestionText("What is the signature for the main method?");
        qCode.setOptions(Arrays.asList("main()", "public void main", "public static void main(String[] args)", "static main"));
        qCode.setCorrectOptionIndex(2);
        code.addQuestion(qCode);
        service.add(code);


        ReadingMaterial sport = new ReadingMaterial();
        sport.setTitle("Basketball Basic");
        sport.setCategory("Sport");
        sport.setContent("Basketball teams consist of five players on the court.");
        sport.setProgress(0);
        sport.setTimeLimit(30);

        Question qSport = new Question();
        qSport.setQuestionText("How many players per team on the court?");
        qSport.setOptions(Arrays.asList("3", "5", "7", "11"));
        qSport.setCorrectOptionIndex(1);
        sport.addQuestion(qSport);
        service.add(sport);
    }
}