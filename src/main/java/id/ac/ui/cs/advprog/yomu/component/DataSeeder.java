package id.ac.ui.cs.advprog.yomu.component;

import id.ac.ui.cs.advprog.yomu.model.Question;
import id.ac.ui.cs.advprog.yomu.model.ReadingMaterial;
import id.ac.ui.cs.advprog.yomu.service.ReadingMaterialService;
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
    public void run(String... args) {
        ReadingMaterial math = new ReadingMaterial();
        math.setTitle("Quadratic Equations 101");
        math.setCategory("Mathematics");
        math.setContent("The standard form is $ax^2 + bx + c = 0$. The discriminant is $D = b^2 - 4ac$.");

        Question q1 = new Question();
        q1.setQuestionText("What is the standard form of a quadratic equation?");
        q1.setOptions(Arrays.asList("ax+b=0", "ax^2+bx+c=0", "x=y", "a+b=c"));
        q1.setCorrectOptionIndex(1);

        math.getQuestions().add(q1);
        service.add(math);
    }
}