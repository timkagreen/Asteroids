package main;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class AsteroidsApp extends Application {
    private int score = 0;

    private Pane root;

    private List<GameObject> bullets = new ArrayList<>();
    private List<GameObject> enemies = new ArrayList<>();
    private List<GameObject> busters = new ArrayList<>();

    private GameObject player;


    private int boost = 0;
    Date date1 = new Date();



    private Parent createContent(){
        root = new Pane();
        root.setPrefSize(600,600);
        BackgroundImage myBI= new BackgroundImage(new Image("space.png",500,500,false,true),
                BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT,
                BackgroundSize.DEFAULT);
        root.setBackground(new Background(myBI));


        player = new Player();

        player.setVelocity(new Point2D(1, 0));

        addGameObject(player, 300, 300);

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long l) {
                onUpdate();
            }
        };
        timer.start();

        return root;
    }

    private void boostTimer(){
        Date date2 = new Date();
        if (date2.getTime() - date1.getTime() > 5000){
            boost = 0;
        }
    }

    private void addBullet(GameObject bullet, double x, double y){
        bullets.add(bullet);
        addGameObject(bullet, x+18, y+5);
    }

    private void addBullet2(GameObject bullet,GameObject bullet2, double x, double y){
        bullets.add(bullet);
        bullets.add(bullet2);
        addGameObject(bullet, x+0, y-5);
       addGameObject(bullet2, x+10, y+10);
    }

    private void addEnemy(GameObject enemy, double x, double y){
        enemies.add(enemy);
        if (!(Math.abs(player.getView().getTranslateX() - x) < 50) && !(Math.abs(player.getView().getTranslateY() - y) < 50))
            addGameObject(enemy, x, y);
    }

    private void addBuster(GameObject buster, double x, double y){
        busters.add(buster);
        addGameObject(buster, x, y);
    }

    private void addGameObject(GameObject object, double x, double y){
        object.getView().setTranslateX(x);
        object.getView().setTranslateY(y);
        root.getChildren().add(object.getView());
    }


    private void onUpdate(){
        for (GameObject bullet : bullets){
            for (GameObject enemy : enemies){
                if(bullet.isCollodoing(enemy)){
                    bullet.setAlive(false);
                    enemy.setAlive(false);
                    score++;

                    root.getChildren().removeAll(bullet.getView(), enemy.getView());
                }
            }

        }

        for (GameObject enemy : enemies){
            if(enemy.isCollodoing(player)){
                player.setAlive(false);
                enemy.setAlive(false);
                System.out.println("набрано " + score + " очков");
                System.exit(-1);

                root.getChildren().removeAll(player.getView(),enemy.getView());
            }
        }

        for (GameObject buster : busters){
            if(buster.isCollodoing(player)){
                buster.setAlive(false);
                score+=2;
                boost = 1;
                date1 = new Date();

                root.getChildren().removeAll(buster.getView());
            }
        }

        boostTimer();



        bullets.removeIf(GameObject::isDead);
        enemies.removeIf(GameObject::isDead);
        busters.removeIf(GameObject::isDead);


        bullets.forEach(GameObject::update);
        enemies.forEach(GameObject::update);
        busters.forEach(GameObject::update);

        player.update();

        if (Math.random() < 0.015){
            addEnemy(new Enemy(), Math.random() * root.getPrefWidth(), Math.random() * root.getPrefHeight());
        }

        if (Math.random() < 0.005){
            if (busters.size() < 3) {
                addBuster(new Buster(), Math.random() * root.getPrefWidth(), Math.random() * root.getPrefHeight());
            }
        }
    }

    public static class Player extends GameObject{

        Player() {
            super(getShape());
        }

        private static Rectangle getShape() {

            String imageBG = "player2.png";
            Rectangle ship = new Rectangle(47,24);
            ship.setFill(new ImagePattern(new Image(imageBG)));

            return ship;
        }

    }

    private static class Enemy extends GameObject{
        Enemy() {
            super(getShape());
        }

        private static Circle getShape() {
            String imageBG = "enemy.png";
            Circle enemy = new Circle(15,15, 15);
            enemy.setFill(new ImagePattern(new Image(imageBG)));
            return enemy;
        }
    }

    private static class Bullet extends GameObject{
        Bullet() {
            super(getShape());
        }

        private static Circle getShape() {
            String imageBG = "bullet.png";
            Circle enemy = new Circle(6,6, 6);
            enemy.setFill(new ImagePattern(new Image(imageBG)));
            return enemy;
        }
    }

    private static class Buster extends GameObject{
        Buster() {
            super(getShape());
        }

        private static Circle getShape() {
            String imageBG = "buster.png";
            Circle enemy = new Circle(8,8, 8);
            enemy.setFill(new ImagePattern(new Image(imageBG)));
            return enemy;
        }
    }

    @Override
    public void start(Stage stage) throws Exception {

        stage.setScene(new Scene(createContent()));
        stage.getScene().setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.LEFT){
                player.rotateLeft();
            } else if (e.getCode() == KeyCode.RIGHT){
                player.rotateRight();
            } else if (e.getCode() == KeyCode.SPACE && boost == 0){
                Bullet bullet = new Bullet();
                bullet.setVelocity(player.getVelocity().normalize().multiply(5));
                addBullet(bullet, player.getView().getTranslateX(),player.getView().getTranslateY());
            } else if (e.getCode() == KeyCode.SPACE){
                Bullet bullet = new Bullet();
                bullet.setVelocity(player.getVelocity().normalize().multiply(5));
                Bullet bullet2 = new Bullet();
                bullet2.setVelocity(player.getVelocity().normalize().multiply(5));
                addBullet2(bullet,bullet2, player.getView().getTranslateX(),player.getView().getTranslateY());}
        });
        stage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }
}
