# -*- coding: utf-8 -*-
"""
Created on Fri Jun  5 03:41:45 2020

@author: duy19
"""
import sys
import json


def main():

    TS = 0
    TJ = 0
    counter = 0
    with open(sys.argv[1]) as f:
        for line in f:
            counter += 1
            TS += json.loads(line)["ts"]
            TJ += json.loads(line)["tj"]

    TS = TS / counter
    TJ = TJ / counter
    
    print("TS:", TS * 0.000001, "ms")
    print("TJ:", TJ * 0.000001, "ms")
    
if __name__ == "__main__":
    main()
