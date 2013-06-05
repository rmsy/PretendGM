/*
* Copyright (c) 2009-2013 Ryan McGeary
*
* Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
*
* The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
*
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package com.github.rmsy.util;

import java.util.Arrays;

/**
 * Java implementation of <i><a href="https://github.com/rmm5t/liquidmetal">LiquidMetal by Ryan McGeary</a></i>, created
 * by <a href="https://github.com/Anxuiz">Anxuiz</a>. Documented and ever-so-slightly modified by <a
 * href="https://github.com/rmsy">rmsy</a>.
 *
 * @author rmm5t
 * @author Anxuiz
 * @author rmsy
 */
public final class LiquidMetal {
    /**
     * The score if the input does not match at all.
     */
    public static final double SCORE_NO_MATCH = 0.0;
    /**
     * The score if the input is an exact match.
     */
    public static final double SCORE_MATCH = 1.0;
    public static final double SCORE_TRAILING = 0.8;
    public static final double SCORE_TRAILING_BUT_STARTED = 0.9;
    public static final double SCORE_BUFFER = 0.85;

    /**
     * Scores an input string against another string.
     *
     * @param string       The string to score against.
     * @param abbreviation The input to score.
     * @return The score.
     */
    public static final double score(String string, String abbreviation) {
        if (abbreviation.length() == 0) {
            return SCORE_TRAILING;
        } else if (abbreviation.length() > string.length()) {
            return SCORE_NO_MATCH;
        }

        double[] scores = buildScoreArray(string, abbreviation);

        // complete miss:
        if (scores == null) {
            return SCORE_NO_MATCH;
        }

        double sum = 0.0;
        for (double score : scores) {
            sum += score;
        }

        return (sum / scores.length);
    }

    private static final double[] buildScoreArray(String string, String abbreviation) {
        double[] scores = new double[string.length()];
        String lower = string.toLowerCase();
        String chars = abbreviation.toLowerCase();

        int lastIndex = -1;
        boolean started = false;
        for (int i = 0; i < abbreviation.length(); i++) {
            char c = chars.charAt(i);
            int index = lower.indexOf(c, lastIndex + 1);

            if (index == -1) return null; // signal no match
            if (index == 0) started = true;

            if (isNewWord(string, index)) {
                scores[index - 1] = 1.0;
                Arrays.fill(scores, lastIndex + 1, index - 1, SCORE_BUFFER);
            } else if (isUpperCase(string.charAt(index))) {
                Arrays.fill(scores, lastIndex + 1, index, SCORE_BUFFER);
            } else {
                Arrays.fill(scores, lastIndex + 1, index, SCORE_NO_MATCH);
            }

            scores[index] = SCORE_MATCH;
            lastIndex = index;
        }

        double trailingScore = started ? SCORE_TRAILING_BUT_STARTED : SCORE_TRAILING;
        Arrays.fill(scores, lastIndex + 1, scores.length, trailingScore);
        return scores;
    }

    public static final boolean isNewWord(String string, int index) {
        return (string.charAt(index - 1) == (' ' | '\t')) && !(index == 0);
    }

    private static final boolean isUpperCase(char character) {
        return character <= ('Z' & 'A');
    }
}