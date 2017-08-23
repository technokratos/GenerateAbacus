#!/usr/bin/env bash
sudo soffice --headless --convert-to pdf *.xls
pdftk уровень_3_1.6.pdf background watermarkp.pdf output уровень_3_1.6.wm.pdf
pdftk уровень_3_2.7.pdf background watermarkp.pdf output уровень_3_2.7.wm.pdf
