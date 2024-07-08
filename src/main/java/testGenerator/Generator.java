package testGenerator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class Generator {
    //start - начальный момент времени, если задано -1 выбирается от 0 до 100
    //h - шаг времени, если 0 и меньше, выбирается в пределах от 1 до 5;
    //i - кол-во строк, если 0 и меньше, то выбирается от 10 до 1000
    //count - количество созданных файлов (старые будут удалены)


    Generator(int start,int h, int i,int count){
        Random rnd = new Random();
        if (start <=-1){
            start=rnd.nextInt(101);
        }
        if (h<=0){
            h=1+rnd.nextInt(5);
        }
        if (i<=0){
            i=10+rnd.nextInt(991);
        }

        File directory = new File(System.getProperty("user.dir"),"testData");
        if (!directory.exists()){
                directory.mkdir();
        }
        deleteAllFilesFolder(directory.getPath());

        for (int N = 0;N<count;N++) {
            String filename = "trajectory" + N +".txt";
            StringBuilder data= new StringBuilder();
            for (int j = 0; j < i; j++) {
                data.append(start).append(".000  ").append(rnd.nextInt(100000)).append(".").append(rnd.nextInt(10)).append("  ").append(rnd.nextInt(100000)).append(".").append(rnd.nextInt(10)).append("  ").append(rnd.nextInt(100000)).append(".").append(rnd.nextInt(10)).append("  ").append(rnd.nextInt(200)).append(".").append(100 + rnd.nextInt(900)).append("  ").append(rnd.nextInt(200)).append(".").append(100 + rnd.nextInt(900)).append("  ").append(rnd.nextInt(200)).append(".").append(100 + rnd.nextInt(900));
                data.append("\n");
                start += h;
            }
            try(FileWriter writer = new FileWriter(directory.getPath()+"/"+filename,false)){
                writer.write(data.toString());
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }


    public void deleteAllFilesFolder(String path) {
        for (File myFile : new File(path).listFiles())
            if (myFile.isFile()) myFile.delete();
    }
}
