/*
* Copyright (C) 2017 John Magdalinos
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*  	http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.example.android.bakeit.utilities;

import android.content.ContentValues;
import android.database.Cursor;

import com.example.android.bakeit.data.DbContract.StepsEntry;
import com.example.android.bakeit.data.Step;

import java.text.Normalizer;
import java.util.ArrayList;

/**
 * Helper class with a list of methods and constants used throughout the app
 */

public class DataUtilities {

    /** Keys used throughout the app */
    public static final String KEY_RECIPE_NAME = "recipe_name";
    public static final String KEY_RECIPE_SERVINGS = "recipe_servings";
    public static final String KEY_RECIPE_IMAGE = "recipe_image";
    public static final String KEY_STEP_ID = "step";
    public static final String KEY_STEPS = "steps";
    public static final String KEY_IS_TWO_PANE = "is_tablet";
    public static final String KEY_FRAGMENT = "fragment";


    /** Converts an ArrayList<ContentValues> to a ContentValues[] object */
    public static ContentValues[] convertArrayListToContentValues(ArrayList<ContentValues> data) {
        // Array storing the data after the conversion
        if (data == null || data.size() == 0) return null;
        ContentValues[] finalData = new ContentValues[data.size()];

        // Iterate through all ContentValue objects and store the in the ContentValues[]
        for (int i = 0; i < data.size(); i++) {
            finalData[i] = data.get(i);
        }
        return finalData;
    }

    /** Creates a summary of all ingredients */
    public static String createIngredientSummary(String name, String quantity, String measure) {
        // Create a StringBuilder object in which to append all ingredients
        StringBuilder summary = new StringBuilder();

        // Capitalize the first letter of the name
        String correctedName = getCorrectedName(name);
        // Correct the measure and add it to the quantity
        String correctedMeasure = getCorrectedMeasure(quantity, measure);

        // Append the corrected strings to the StringBuilder
        summary.append(correctedName);
        summary.append(", ");
        summary.append(correctedMeasure);

        return summary.toString();
    }

    /** Capitalizes first letter of an ingredient's name */
    private static String getCorrectedName(String input) {
        // Check that the input is at least 1 character long to avoid an exception
        if (input.length() < 2) return " ";
        // Capitalize the first letter only
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }

    /**
     * Converts the quantity to an integer or a double depending on whether there is a decimal
     * part to the quantity. It then creates the plural of the measure if the quantity is larger
     * than 1 and returns the quantity plus the correct measure.
     */
    private static String getCorrectedMeasure(String quantity, String measure) {
        // Create a StringBuilder object in which to append all quantities and measures
        StringBuilder builder = new StringBuilder();

        // Boolean stating if the quantity is larger than 1
        boolean needsPlural = false;

        try {
            // Convert quantity to long
            double doubleQuantity = Double.parseDouble(quantity.replace(",", ".").trim());

            // Get the integer part of the quantity
            int intQuantity = (int) doubleQuantity;

            // Get the decimal part of the quantity
            double decimalQuantity = doubleQuantity - intQuantity;

            // Check if the decimal part is 0
            if (decimalQuantity != 0) {
                // Append the initial quantity
                builder.append(String.valueOf(doubleQuantity));
            } else{
                // Append the integer quantity
                builder.append(String.valueOf(intQuantity));
            }

            // Append a space after the quantity
            builder.append(" ");


            // Check if the quantity is larger than 1
            needsPlural = (doubleQuantity > 1);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        // Convert measure to plural if needed
        String finalMeasure;
        switch (measure) {
            case "CUP":
                if (needsPlural) {
                    finalMeasure = "cups";
                } else {
                    finalMeasure = "cup";
                }
                break;
            case "TBLSP":
                if (needsPlural) {
                    finalMeasure = "tablespoons";
                } else {
                    finalMeasure = "tablespoon";
                }
                break;
            case "TSP":
                if (needsPlural) {
                    finalMeasure = "teaspoons";
                } else {
                    finalMeasure = "teaspoon";
                }
                break;
            case "K":
                if (needsPlural) {
                    finalMeasure = "kgs";
                } else {
                    finalMeasure = "kg";
                }
                break;
            case "G":
                if (needsPlural) {
                    finalMeasure = "grs";
                } else {
                    finalMeasure = "gr";
                }
                break;
            case "OZ":
                finalMeasure = "oz";
                break;
            case "UNIT":
                if (needsPlural) {
                    finalMeasure = "units";
                } else {
                    finalMeasure = "unit";
                }
                break;
            default:
                finalMeasure = measure;
                break;
        }

        // Append the measure to the quantity
        builder.append(finalMeasure);
        return builder.toString();
    }

    /** Converts a cursor of steps to an ArrayList of Step objects **/
    public static ArrayList<Step> convertCursorToArrayList(Cursor cursor) {
        // If the cursor is empty, return early
        if (cursor == null || cursor.getCount() == 0) return null;
        // Create the ArrayList of Step objects
        ArrayList<Step> steps = new ArrayList<>();

        // Get the column indexes
        int videoColumnIndex = cursor.getColumnIndex(StepsEntry.COLUMN_VIDEO);
        int shortDescColumnIndex = cursor.getColumnIndex(StepsEntry.COLUMN_SHORT_DESCRIPTION);
        int descColumnIndex = cursor.getColumnIndex(StepsEntry.COLUMN_DESCRIPTION);

        // Iterate through all cursor entries and store them in the ArrayList
        for (int i = 0; i < cursor.getCount(); i++) {
            // Move the cursor to the current position
            cursor.moveToPosition(i);

            // Create a new Step object
            Step currentStep = new Step(
                    cursor.getString(shortDescColumnIndex),
                    cursor.getString(descColumnIndex),
                    cursor.getString(videoColumnIndex),
                    null);

            // Add the current Step to the ArrayList
            steps.add(i, currentStep);
        }
        return steps;
    }

    /** Removes the numbering from a step */
    public static String removeNumbering(String input) {
        // Check if the first character is a number; if not, return the text as is
        if (Character.isDigit(input.charAt(0))) {
            String output;
            // Find the position of the "." in the text
            int dotIndex = input.indexOf(".");

            // Check if there is a "." in the text (not -1)
            if (dotIndex != -1) {
                // The numbering format is e.g. "1. Text"; we have to remove a number of
                // characters equal to the position of the dot plus 2.
                output = input.substring((dotIndex + 2));
            } else {
                // Don't change the text
                output = input;
            }
            return output;
        } else return input;
    }

    /** Removes all non-unicode characters from a text */
    public static String checkText(String input) {
        // Normalizes text
        String output = Normalizer.normalize(input, Normalizer.Form.NFKC);
        // Removes all non uniform characters
        output = output.replaceAll("[^\\p{ASCII}]", "");

        return output;
    }
}
