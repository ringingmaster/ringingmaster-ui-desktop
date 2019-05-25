package org.ringingmaster.ui.desktop;

import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestRxJava {

    public static void main(String[] args) {

        final Logger log = LoggerFactory.getLogger(TestRxJava.class);


        BehaviorSubject<Integer> observableSelectedIndex = BehaviorSubject.createDefault(-1);
        BehaviorSubject<String> selectedNotation = BehaviorSubject.create();

        new Thread(() -> {
            for (int i=0;i<20;i++) {

                observableSelectedIndex.onNext(i);
                try {
                    Thread.sleep(4000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        new Thread(() -> {
            for (int i=0;i<1;i++) {

                char c = (char)(35 + i);
                selectedNotation.onNext(Character.toString(c));

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();


        observableSelectedIndex.subscribe(index -> log.info("selectedIndex = [{}]", index));
        selectedNotation.subscribe(notation -> log.info("selectedNotation = [{}]", notation));

//        Observable<String> triggeredNotation = Observable.combineLatest(observableSelectedIndex, selectedNotation, (integer, s) -> s);


        Observable<String> mappedNotation = selectedNotation.map(n -> n);

//        Observable<String> triggeredNotation = observableSelectedIndex.map(integer -> {
//            return mappedNotation.last("").blockingGet();
//        });

        Observable<String> triggeredNotation = observableSelectedIndex.withLatestFrom(mappedNotation, (integer, s) -> s);


        triggeredNotation.subscribe(notation -> log.info("    TriggeredNotation = [{}]", notation));


        try {
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
