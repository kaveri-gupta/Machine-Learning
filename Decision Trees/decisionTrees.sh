#!/bin/bash

javac DecisionTrees/*.java
echo "========================"
echo "Setting A Question 1(b)"
echo "========================"
java -cp DecisionTrees MainClass mushroom.names SettingA/training.data SettingA/training.data 
echo "========================"
echo
echo

echo "========================"
echo "Setting A Question 1(c)"
echo "========================"
java -cp DecisionTrees MainClass mushroom.names SettingA/training.data SettingA/test.data 
echo "========================"
echo
echo

echo "========================"
echo "Setting A Question 2"
echo "========================"
java -cp DecisionTrees MainClass mushroom.names SettingA/training.data SettingA/test.data SettingA/CVSplits 1 2 3 4 5 10 15 20 
echo "========================"
echo
echo

echo "========================"
echo "Setting B Question 1(a)"
echo "========================"
java -cp DecisionTrees MainClass mushroom.names SettingB/training.data SettingB/training.data
echo "========================"
echo
echo

echo "========================"
echo "Setting B Question 1(b)"
echo "========================"
java -cp DecisionTrees MainClass mushroom.names SettingB/training.data SettingB/test.data 
echo "========================"
echo
echo

echo "========================"
echo "Setting B Question 1(c)"
echo "========================"
java -cp DecisionTrees MainClass mushroom.names SettingB/training.data SettingA/training.data 
echo "========================"
echo
echo

echo "========================"
echo "Setting B Question 1(d)"
echo "========================"
java -cp DecisionTrees MainClass mushroom.names SettingB/training.data SettingA/test.data 
echo "========================"
echo
echo

echo "========================"
echo "Setting B Question 2"
echo "========================"
java -cp DecisionTrees MainClass mushroom.names SettingB/training.data SettingB/test.data SettingB/CVSplits 1 2 3 4 5 10 15 20 
echo "========================"
echo
echo

echo "========================"
echo "Setting C"
echo "========================"
java -cp DecisionTrees MainClass mushroom.names SettingC/training.data SettingC/test.data SettingC/CVSplits -1 
echo "========================"
echo
echo


