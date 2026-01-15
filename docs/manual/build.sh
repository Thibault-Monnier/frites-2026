#!/bin/bash

cd sources
pdflatex -interaction=nonstopmode -shell-escape main.tex
pdflatex -interaction=nonstopmode -shell-escape main.tex
mv main.pdf ../manual.pdf
rm *.aux *.log *.toc *.out
cd ..
echo "PDF manual generated at ./manual.pdf"