package com.example.dex.letsbake.utils;

import android.content.ContentValues;

import com.example.dex.letsbake.database.IngredientsColumns;
import com.example.dex.letsbake.database.RecipeColumns;
import com.example.dex.letsbake.database.StepsColumns;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class BakingJsonUtils {

    public static final int INDEX_RECIPES = 0;
    public static final int INDEX_INGREDIENTS = 1;
    public static final int INDEX_STEPS = 2;


    public static ArrayList<ContentValues[]> getResultList(String jsonStr)
            throws JSONException {

        final String RECIPE_ID = "id";
        final String RECIPE_NAME = "name";
        final String RECIPE_INGREDIENTS = "ingredients";
        final String RECIPE_STEPS = "steps";
        final String RECIPE_SERVINGS = "servings";
        final String RECIPE_IMAGE = "image";

        final String RECIPE_INGREDIENTS_QUANTITY = "quantity";
        final String RECIPE_INGREDIENTS_MEASURE = "measure";
        final String RECIPE_INGREDIENTS_INGREDIENT = "ingredient";

        final String RECIPE_STEPS_ID = "id";
        final String RECIPE_STEPS_SHORT_DESCRIPTION = "shortDescription";
        final String RECIPE_STEPS_DESCRIPTION = "description";
        final String RECIPE_STEPS_VIDEO_URL = "videoURL";
        final String RECIPE_STEPS_THUMBNAIL_URL = "thumbnailURL";


        JSONArray jsonRecipesArray = new JSONArray(jsonStr);

        ArrayList<ContentValues[]> jsonResult = new ArrayList<>();

        ArrayList<ContentValues> recipeList = new ArrayList<>();
        ArrayList<ContentValues> ingredientList = new ArrayList<>();
        ArrayList<ContentValues> stepList = new ArrayList<>();


        for (int i = 0; i < jsonRecipesArray.length(); i++) {
            JSONObject recipeData = jsonRecipesArray.getJSONObject(i);

            ContentValues rV = new ContentValues();
            rV.put(RecipeColumns.ID, recipeData.getInt(RECIPE_ID));
            rV.put(RecipeColumns.NAME, recipeData.getString(RECIPE_NAME));


            // INGREDIENTS
            JSONArray jsonIngredientsArray = recipeData.getJSONArray(RECIPE_INGREDIENTS);

            for (int j = 0; j < jsonIngredientsArray.length(); j++) {
                JSONObject ingredientsData = jsonIngredientsArray.getJSONObject(j);

                ContentValues iV = new ContentValues();
                iV.put(IngredientsColumns.RECIPE_ID, recipeData.getInt(RECIPE_ID));
                iV.put(IngredientsColumns.QUANTITY, ingredientsData.getDouble(RECIPE_INGREDIENTS_QUANTITY));
                iV.put(IngredientsColumns.MEASURE, ingredientsData.getString(RECIPE_INGREDIENTS_MEASURE));
                iV.put(IngredientsColumns.INGREDIENT, ingredientsData.getString(RECIPE_INGREDIENTS_INGREDIENT));

                ingredientList.add(iV);
            }

            rV.put(RecipeColumns.INGREDIENTS, jsonIngredientsArray.length());

            // STEPS
            JSONArray jsonStepsArray = recipeData.getJSONArray(RECIPE_STEPS);

            for (int j = 0; j < jsonStepsArray.length(); j++) {
                JSONObject stepsData = jsonStepsArray.getJSONObject(j);

                ContentValues sV = new ContentValues();
                sV.put(StepsColumns.RECIPE_ID, recipeData.getInt(RECIPE_ID));
                sV.put(StepsColumns.STEP, stepsData.getInt(RECIPE_STEPS_ID));
                sV.put(StepsColumns.SHORT_DESCRIPTION, stepsData.getString(RECIPE_STEPS_SHORT_DESCRIPTION));
                sV.put(StepsColumns.DESCRIPTION, stepsData.getString(RECIPE_STEPS_DESCRIPTION));
                sV.put(StepsColumns.VIDEO_URL, stepsData.getString(RECIPE_STEPS_VIDEO_URL));
                sV.put(StepsColumns.THUMBNAIL_URL, stepsData.getString(RECIPE_STEPS_THUMBNAIL_URL));

                stepList.add(sV);
            }

            rV.put(RecipeColumns.STEPS, jsonStepsArray.length());
            rV.put(RecipeColumns.SERVINGS, recipeData.getInt(RECIPE_SERVINGS));
            rV.put(RecipeColumns.IMAGE, recipeData.getString(RECIPE_IMAGE));

            recipeList.add(rV);
        }

        jsonResult.add(recipeList.toArray(new ContentValues[recipeList.size()]));
        jsonResult.add(ingredientList.toArray(new ContentValues[ingredientList.size()]));
        jsonResult.add(stepList.toArray(new ContentValues[stepList.size()]));

        return jsonResult;
    }
}