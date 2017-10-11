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

package com.example.android.bakeit.Utilities;

import android.content.ContentValues;
import android.text.TextUtils;

import com.example.android.bakeit.Data.DbContract.IngredientsEntry;
import com.example.android.bakeit.Data.DbContract.RecipesEntry;
import com.example.android.bakeit.Data.DbContract.StepsEntry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Utilities for parsing the JSON data
 */

public class JSONUtilities {

    /** Keys used to parse the JSON response */
    private static final String KEY_RECIPE_NAME = "name";
    private static final String KEY_INGREDIENTS = "ingredients";
    private static final String KEY_QUANTITY = "quantity";
    private static final String KEY_MEASURE = "measure";
    private static final String KEY_INGREDIENT_NAME = "ingredient";
    private static final String KEY_STEPS = "steps";
    private static final String KEY_SHORT_DESC = "shortDescription";
    private static final String KEY_DESC = "description";
    private static final String KEY_VIDEO = "videoURL";
    private static final String KEY_THUMB = "thumbnailURL";
    private static final String KEY_SERVINGS = "servings";
    private static final String KEY_IMAGE = "image";

    /** Keys for storing the final ArrayList */
    public static final int ID_RECIPES = 0;
    public static final int ID_INGREDIENTS = 1;
    public static final int ID_STEPS = 2;

    /** Replacement images */
    private static final String NUTELLA_PIE_IMAGE_URL = "https://www.recipeboy" +
            ".com/wp-content/uploads/2016/09/No-Bake-Nutella-Pie.jpg";
    private static final String BROWNIES_IMAGE_URL = "https://cafedelites.com/wp-content/uploads/2016/08/Fudgy-Cocoa-Brownies-35.jpg";
    private static final String YELLOW_CAKE_IMAGE_URL = "https://assets.epicurious.com/photos/57c5b45184c001120f616523/master/pass/moist-yellow-cake.jpg";
    private static final String CHEESECAKE_IMAGE_URL = "http://food.fnr.sndimg" +
            ".com/content/dam/images/food/fullset/2013/12/9/0/FNK_Cheesecake_s4x3.jpg.rend.hgtvcom.616.462.suffix/1387411272847.jpeg";


    /** Returns an ArrayList with all the data */
    public static ArrayList<ArrayList<ContentValues>> getData(String jsonResponse) throws JSONException {
        // If the response is null (an error has occurred) exit early
        if (jsonResponse == null) return null;

        ArrayList<ContentValues> mRecipes = new ArrayList<>();
        ArrayList<ContentValues> mIngredients = new ArrayList<>();
        ArrayList<ContentValues> mSteps = new ArrayList<>();

        // Create the JSON Array with the recipes
        JSONArray recipesArray = new JSONArray(jsonResponse);

        // Iterate through all recipes
        for (int i = 0; i < recipesArray.length(); i++) {
            // Temporary variables that will hold the parsed results
            String recipeName, servings, image;

            // Get the recipe at position i
            JSONObject currentRecipe = recipesArray.getJSONObject(i);

            // Save the data in the temporary variables
            recipeName = currentRecipe.getString(KEY_RECIPE_NAME);
            servings = currentRecipe.getString(KEY_SERVINGS);
            image = currentRecipe.getString(KEY_IMAGE);
            if (TextUtils.isEmpty(image) || image.equals("")) {
                switch (recipeName) {
                    case "Nutella Pie":
                        image = NUTELLA_PIE_IMAGE_URL;
                        break;
                    case "Brownies":
                        image = BROWNIES_IMAGE_URL;
                        break;
                    case "Yellow Cake":
                        image = YELLOW_CAKE_IMAGE_URL;
                        break;
                    case "Cheesecake":
                        image = CHEESECAKE_IMAGE_URL;
                        break;
                }
            }

            // Get the ingredients' array
            JSONArray ingredientsArray = currentRecipe.getJSONArray(KEY_INGREDIENTS);

            // Iterate through all ingredients
            for (int j = 0; j < ingredientsArray.length(); j++) {
                // Temporary variables that will hold the parsed results
                String quantity, measure, ingredientName;

                // Get the ingredient at position j
                JSONObject currentIngredient = ingredientsArray.getJSONObject(j);

                // Save the data in the temporary variables
                quantity = currentIngredient.getString(KEY_QUANTITY);
                measure = currentIngredient.getString(KEY_MEASURE);
                ingredientName = currentIngredient.getString(KEY_INGREDIENT_NAME);

                // Put the ingredient data in a ContentValues object
                ContentValues ingredient = new ContentValues();
                ingredient.put(IngredientsEntry.COLUMN_QUANTITY, quantity);
                ingredient.put(IngredientsEntry.COLUMN_MEASURE, measure);
                ingredient.put(IngredientsEntry.COLUMN_NAME, ingredientName);
                ingredient.put(IngredientsEntry.COLUMN_RECIPE, recipeName);

                // Add the ingredient to the ArrayList
                mIngredients.add(ingredient);
            }

            // Get the steps' array
            JSONArray stepsArray = currentRecipe.getJSONArray(KEY_STEPS);

            // Iterate through all steps
            for (int k = 0; k < stepsArray.length(); k++) {
                // Temporary variables that will hold the parsed results
                String shortDesc, desc, video, thumb;

                // Get the step at position k
                JSONObject currentStep = stepsArray.getJSONObject(k);

                // Save the data in the temporary variables
                shortDesc = currentStep.getString(KEY_SHORT_DESC);
                shortDesc = DataUtilities.checkText(shortDesc);
                desc = currentStep.getString(KEY_DESC);
                desc = DataUtilities.checkText(desc);
                video = currentStep.getString(KEY_VIDEO);
                thumb = currentStep.getString(KEY_THUMB);

                // Put the step data in a ContentValues object
                ContentValues step = new ContentValues();
                step.put(StepsEntry.COLUMN_SHORT_DESCRIPTION, shortDesc);
                step.put(StepsEntry.COLUMN_DESCRIPTION, desc);
                step.put(StepsEntry.COLUMN_VIDEO, video);
                step.put(StepsEntry.COLUMN_THUMBNAIL, thumb);
                step.put(StepsEntry.COLUMN_RECIPE, recipeName);

                // Add the ingredient to the ArrayList
                mSteps.add(step);
            }

            // Put the recipe data in a ContentValues object
            ContentValues recipe = new ContentValues();
            recipe.put(RecipesEntry.COLUMN_NAME, recipeName);
            recipe.put(RecipesEntry.COLUMN_SERVINGS, servings);
            recipe.put(RecipesEntry.COLUMN_IMAGE, image);

            // Add the recipe to the ArrayList
            mRecipes.add(recipe);
        }

        // Store the 3 ArrayLists in a new ArrayList
        ArrayList<ArrayList<ContentValues>> allData = new ArrayList<>();
        allData.add(ID_RECIPES, mRecipes);
        allData.add(ID_INGREDIENTS, mIngredients);
        allData.add(ID_STEPS, mSteps);
        return allData;
    }
}
