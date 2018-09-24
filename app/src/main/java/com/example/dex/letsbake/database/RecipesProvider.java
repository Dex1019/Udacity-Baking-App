package com.example.dex.letsbake.database;

import android.net.Uri;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.InexactContentUri;
import net.simonvt.schematic.annotation.TableEndpoint;


@ContentProvider(
        authority = RecipesProvider.AUTHORITY,
        database = RecipesDatabase.class,
        packageName = "com.example.dex.letsbake.provider"
)
public final class RecipesProvider {

    public static final String AUTHORITY = "com.example.dex.letsbake.provider.RecipesProvider";
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);


    private RecipesProvider() {
    }

    private static Uri buildUri(String... paths) {
        Uri.Builder builder = BASE_CONTENT_URI.buildUpon();
        for (String path : paths) {
            builder.appendPath(path);
        }
        return builder.build();
    }

    interface Path {
        String RECIPES = "recipes";
        String INGREDIENTS = "ingredients";
        String STEPS = "steps";
    }

    @TableEndpoint(table = RecipesDatabase.Tables.RECIPES)
    public static class Recipes {

        @ContentUri(
                path = Path.RECIPES,
                type = "vnd.android.cursor.dir/recipes",
                defaultSort = RecipeColumns.ID + " ASC"
        )
        public static final Uri CONTENT_URI = buildUri(Path.RECIPES);
    }

    @TableEndpoint(table = RecipesDatabase.Tables.INGREDIENTS)
    public static class Ingredients {

        @ContentUri(
                path = Path.INGREDIENTS,
                type = "vnd.android.cursor.dir/ingredients"
        )
        public static final Uri CONTENT_URI = buildUri(Path.INGREDIENTS);

        @InexactContentUri(
                path = Path.INGREDIENTS + "/#",
                name = "RECIPE_INGREDIENTS",
                type = "vnd.android.cursor.dir/ingredients",
                whereColumn = IngredientsColumns.RECIPE_ID,
                pathSegment = 1
        )
        public static Uri ingredientsForRecipe(long id) {
            return buildUri(Path.INGREDIENTS, String.valueOf(id));
        }
    }

    @TableEndpoint(table = RecipesDatabase.Tables.STEPS)
    public static class Steps {

        @ContentUri(
                path = Path.STEPS,
                type = "vnd.android.cursor.dir/steps"
        )
        public static final Uri CONTENT_URI = buildUri(Path.STEPS);

        @InexactContentUri(
                path = Path.STEPS + "/#",
                name = "RECIPE_STEPS",
                type = "vnd.android.cursor.dir/steps",
                whereColumn = StepsColumns.RECIPE_ID,
                pathSegment = 1
        )
        public static Uri stepsForRecipe(long id) {
            return buildUri(Path.STEPS, String.valueOf(id));
        }
    }
}