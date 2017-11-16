#!/usr/bin/env bash
sudo soffice --headless --convert-to pdf *.xls
pdftk уровень_2_1.4.pdf background watermarkp.pdf output уровень_2_1.4.wm.pdf
pdftk уровень_2_2.5.pdf background watermarkp.pdf output уровень_2_2.5.wm.pdf

