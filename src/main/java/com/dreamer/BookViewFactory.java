package com.dreamer;

import com.dreamer.util.TafsirView;
import com.dreamer.util.Volume;
import javafx.scene.image.ImageView;

import java.util.Map;

import static java.util.Map.entry;

public class BookViewFactory {
    public static TafsirView createTafsirView(ImageView pageUI) {
        TafsirView tafsirView = new TafsirView(pageUI);

        tafsirView.addVolume(jalalainVolume1());
        tafsirView.addVolume(jalalainVolume4());
        tafsirView.addVolume(jalalainVolume5());

        return tafsirView;
    }

    private static Volume jalalainVolume1() {
        Map<Integer, Integer> pageIndex = Map.ofEntries(
                entry(1, 1)
        );

        return new Volume(1,
                BookViewFactory.class.getResource("Jalalain-Bangla-01.pdf").getFile().substring(1),
                pageIndex, 0);
    }

    private static Volume jalalainVolume4() {
        Map<Integer, Integer> pageIndex = Map.ofEntries(
                entry(293, 9)
        );

        return new Volume(1,
                BookViewFactory.class.getResource("Jalalain-Bangla-04.pdf").getFile().substring(1),
                pageIndex, -3);
    }
    private static Volume jalalainVolume5() {
        Map<Integer, Integer> pageIndex = Map.ofEntries(
                entry(402, 9),
                entry(403, 15),
                entry(404, 20),
                entry(405, 25),
                entry(406, 31),
                entry(407, 34),
                entry(408, 35),
                entry(409, 41),
                entry(410, 49),
                entry(411, 48),
                entry(412, 60),
                entry(413, 71),
                entry(414, 81),
                entry(415, 89),
                entry(416, 97),
                entry(417, 98),
                entry(418, 20),
                entry(419, 20),
                entry(420, 20),
                entry(421, 20),
                entry(422, 140),
                entry(423, 149),
                entry(424, 157),
                entry(425, 159),
                entry(426, 20),
                entry(427, 20)
        );

        return new Volume(1,
                BookViewFactory.class.getResource("Jalalain-Bangla-05.pdf").getFile().substring(1),
                pageIndex, -1);
    }
}
