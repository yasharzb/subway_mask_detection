import random, sys
import lgpio
import time

# Configuration
BUZZER = 18
SERVO = 12
LED = 26

# h = lgpio.gpiochip_open(0)
# lgpio.gpio_claim_output(h, LED)


def my_buz():
    # lgpio.tx_pwm(h, BUZZER, 1000, 50)
    # time.sleep(0.2)
    # lgpio.tx_pwm(h, BUZZER, 1000, 0)
    print('BUZ')


def servo_turn(a):
    # lgpio.tx_pwm(h, SERVO, 50, a)
    # time.sleep(0.2)
    # lgpio.tx_pwm(h, SERVO, 50, 0)
    print('SERVO')


guilty = sys.argv[1]
if guilty == 'True':
    my_buz()
else:
    servo_turn(5)
