package com.DataStructure.Model;

import org.bson.Document;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 点集 数据结构
 */
public class Points implements Serializable {
    public List<Point> points;

    public Points() {
        this.points = new ArrayList<Point>();
    }

    public Points(List<Point> points) {
        this.points = new ArrayList<Point>();
        this.points = points;
    }

    public List<Point> getPoints() {
        return points;
    }

    public void setPoints(List<Point> points) {
        this.points = new ArrayList<Point>();
        this.points = points;
    }

    public boolean add(Point point) {
        return this.points.add(point);
    }

    public List<Document> toDocumnetList() {
        List<Document> documents = new ArrayList<Document>();
        for (Point point : this.getPoints()) {
            Document document = point.toDocumet();
            documents.add(document);
        }
        return documents;
    }

}
