package com.example.dex.letsbake.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.ExecOnCreate;
import net.simonvt.schematic.annotation.IfNotExists;
import net.simonvt.schematic.annotation.OnConfigure;
import net.simonvt.schematic.annotation.OnCreate;
import net.simonvt.schematic.annotation.OnUpgrade;
import net.simonvt.schematic.annotation.Table;


@Database(version = RecipesDatabase.VERSION,
        packageName = "com.example.dex.letsbake.provider")
public final class RecipesDatabase {

    public static final int VERSION = 1;
    @ExecOnCreate
    public static final String EXEC_ON_CREATE = "SELECT * FROM " + Tables.RECIPES;

    private RecipesDatabase() {
    }

    @OnCreate
    public static void onCreate(Context context, SQLiteDatabase db) {

    }

    @OnUpgrade
    public static void onUpgrade(Context context, SQLiteDatabase db, int oldVersion,
                                 int newVersion) {

    }

    @OnConfigure
    public static void onConfigure(SQLiteDatabase db) {

    }

    public static class Tables {
        @Table(RecipeColumns.class)
        @IfNotExists
        public static final String RECIPES = "recipes";

        @Table(IngredientsColumns.class)
        @IfNotExists
        public static final
        String INGREDIENTS = "ingredients";

        @Table(StepsColumns.class)
        @IfNotExists
        public static final String STEPS = "steps";
    }
}