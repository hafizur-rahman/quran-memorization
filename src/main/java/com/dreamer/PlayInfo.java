package com.dreamer;

import com.dreamer.corpus.Range;
import com.dreamer.corpus.Verse;

import java.util.List;
import java.util.Random;

public class PlayInfo {
    private Random rand = new Random(System.currentTimeMillis());

    private Range playRange;

    private List<Verse> verses;

    boolean isStiching = false;

    private int currentVerseIndex;

    private int stichingVerseIndex;

    private int repeatCount = 2;

    private int currentPairRepeated = 0;

    private boolean isPlayAudhubillah = true;
    private boolean isPlayBismillah = true;
    private int currentVerseRepeated;
    private boolean enableStiching;

    public PlayInfo(Range playRange, List<Verse> verses) {
        this.playRange = playRange;
        this.verses = verses;
    }

    public Range getPlayRange() {
        return playRange;
    }

    public Verse getCurrentVerse() {
        if (isStiching) {
            if (currentPairRepeated % 2 == 1) {
                return verses.get(stichingVerseIndex);
            } else {
                return verses.get(stichingVerseIndex-1);
            }
        }

        return verses.get(currentVerseIndex);
    }

    public boolean isPlayAudhubillah() {
        return isPlayAudhubillah;
    }

    public void setPlayAudhubillah(boolean playAudhubillah) {
        isPlayAudhubillah = playAudhubillah;
    }

    public boolean isPlayBismillah() {
        return isPlayBismillah;
    }

    public void setPlayBismillah(boolean playBismillah) {
        isPlayBismillah = playBismillah;
    }

    public boolean isRangeEnded() {
        return currentVerseIndex >= verses.size();
    }

    public void updateRepeatCount(int repeatCount) {
        if (repeatCount >= 1) {
            this.repeatCount = repeatCount;
        }
    }

    public void reset() {
        this.currentVerseIndex = 0;
    }

    public void moveToPrevVerse() {
        if (currentVerseIndex > 0) {
            this.currentVerseIndex--;
            this.currentVerseRepeated = 0;
        }

        if (verses.get(currentVerseIndex).getVerseId() == 1) {
            if (!isPlayBismillah) {
                setPlayBismillah(true);
            }
        }
    }

    public void moveToNextVerse() {
        currentVerseIndex++;
        currentVerseRepeated = 0;

        if (isRangeEnded()) {
            reset();
        }

        if (verses.get(currentVerseIndex).getVerseId() == 1) {
            if (!isPlayBismillah) {
                setPlayBismillah(true);
            }
        }
    }

    public void moveToNextVerseWithRepeating() {
        if (enableStiching && isStiching) {
            currentPairRepeated++;

            if (currentPairRepeated/2 >= repeatCount) {
                isStiching = false;

                currentVerseIndex++;
                currentVerseRepeated = 0;

                if (isRangeEnded()) {
                    reset();
                }

                if (verses.get(currentVerseIndex).getVerseId() == 1) {
                    if (!isPlayBismillah) {
                        setPlayBismillah(true);
                    }
                }
            }
        } else {
            currentVerseRepeated++;

            if (currentVerseRepeated >= repeatCount) {
                currentPairRepeated = 0;
                currentVerseRepeated = 0;

                if (enableStiching && currentVerseIndex > 0 && verses.get(currentVerseIndex).getVerseId() > 1) {
                    isStiching = true;

                    stichingVerseIndex = currentVerseIndex;
                } else {
                    currentVerseIndex++;
                    if (isRangeEnded()) {
                        reset();
                    }

                    if (verses.get(currentVerseIndex).getVerseId() == 1) {
                        if (!isPlayBismillah) {
                            setPlayBismillah(true);
                        }
                    }
                }
            }
        }
    }

    public void setVerses(List<Verse> verses) {
        this.verses = verses;

        reset();

        if (verses.get(currentVerseIndex).getVerseId() == 1) {
            if (!isPlayBismillah) {
                setPlayBismillah(true);
            }
        }
    }

    public void toggleStitching(boolean enableStiching) {
        this.enableStiching = enableStiching;

        if (!enableStiching) {
            this.isStiching = false;
        }
    }

    public int getCurrentVerseRepeated() {
        return currentVerseRepeated;
    }

    public void incrementCurrentVerseRepeatCount() {
        currentVerseRepeated ++;
    }

    public void moveToRandomVerse() {
        while (true) {
            int randomIndex = rand.nextInt(verses.size());

            if (randomIndex != currentVerseIndex) {
                currentVerseIndex = randomIndex;
                currentVerseRepeated = 0;

                break;
            }
        }


        if (isRangeEnded()) {
            reset();
        }
    }
}
