/* Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.engedu.palindromes;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Range;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity {

    private HashMap<Range, PalindromeGroup> findings = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public boolean onFindPalindromes(View view) {
        findings.clear();
        EditText editText = (EditText) findViewById(R.id.editText);
        TextView textView = (TextView) findViewById(R.id.textView);
        String text = editText.getText().toString();
        text = text.replaceAll(" ", "");
        text = text.replaceAll("'", "");
        char[] textAsChars = text.toCharArray();
        if (isPalindrome(textAsChars, 0, text.length())) {
          textView.setText(text + " is already a palindrome!");
        } else {
            PalindromeGroup palindromes = breakIntoPalindromes(text.toCharArray(), 0, text.length());
            textView.setText(palindromes.toString());
        }
        return true;
    }

    private boolean isPalindrome(char[] text, int start, int end) {
        boolean retval = true;
        for (int idx = start; idx <= start + (end - 1 - start)/2; ++idx) {
            if (text[idx] != text[end - 1 - (idx - start)]) {
                retval = false;
            }
        }
        return retval;
    }

    private PalindromeGroup greedyBreakIntoPalindromes(char[] text, int start, int end) {
        PalindromeGroup group = null;
        int startIdx = start;
        while (startIdx < end) {
            for (int endIdx = end; endIdx > startIdx; --endIdx) {
                if (isPalindrome(text, startIdx, endIdx)) {
                    if (group == null) {
                        group = new PalindromeGroup(text, startIdx, endIdx);
                    } else {
                        group.append(new PalindromeGroup(text, startIdx, endIdx));
                    }
                    startIdx = endIdx;
                    break;
                }
            }
        }
        return group;
    }

    private PalindromeGroup recursiveBreakIntoPalindromes(char[] text, int start, int end) {
        PalindromeGroup group = null;

        if (start >= end) {
            return null;
        }

        if (start == end - 1) {
            return new PalindromeGroup(text, start, end);
        }

        PalindromeGroup currGroup = null;
        int smallestGroupSize = 0;
        for (int endIdx = end; endIdx > start; --endIdx) {
            if (isPalindrome(text, start, endIdx)) {
                currGroup = new PalindromeGroup(text, start, endIdx);
                currGroup.append(recursiveBreakIntoPalindromes(text, endIdx, end));

                if (group == null) {
                    group = currGroup;
                    smallestGroupSize = currGroup.length();
                } else if (currGroup.length() < smallestGroupSize) {
                    group = currGroup;
                    smallestGroupSize = currGroup.length();
                }
            }
        }

        return group;
    }

    private PalindromeGroup breakIntoPalindromes(char[] text, int start, int end) {
        PalindromeGroup bestGroup = null;
        // bestGroup = greedyBreakIntoPalindromes(text, start, end);
        bestGroup = recursiveBreakIntoPalindromes(text, start, end);
        return bestGroup;
    }
}
