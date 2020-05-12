package com.kwolarz.hw2;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

class Points {
    public List<Point> points;

    Points() {
        this.points = new ArrayList<>();
    }
}

 class Point {
    private double x;
    private double y;



    Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX(){
         return x;
     }

     public double getY(){
         return y;
     }
}
