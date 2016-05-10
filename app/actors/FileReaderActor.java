package actors;

import akka.actor.UntypedActor;
import play.api.libs.concurrent.Akka;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * Created by rizrusn on 12/04/16.
 */
//public class FileReaderActor extends UntypedActor {
//    public void onReceive(Object message) throws Exception {
//        if (message instanceof FileReaderProtocol) {
//            final String filename = ((FileReaderProtocol) message).
//                    filename;
//            Future<String> future = future(new Callable<String>() {
//                public String call() {
//                    try {
//                        Path path = Paths.get(filename);
//                        List<String> list = Files.readAllLines(path,
//                                StandardCharsets.UTF_8);
//                        String[] contents = list.toArray(new String[list.
//                                size()]);
//                        return Arrays.toString(contents);
//                    } catch(Exception e) {
//                        throw new IllegalStateException(e);
//                    }
//                }
//            }, Akka.system().dispatcher());
//            akka.pattern.Patterns.pipe( future, Akka.system().dispatcher()).to(getSender());
//        }
//    }
//}
